package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION) " +
                "VALUES (?, ?, ?, ?);";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                    connection.prepareStatement(sqlQuery, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            return ps;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return findById(film.getId());
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?" +
                " WHERE ID = ?;";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId());
        return findById(film.getId());
    }

    @Override
    public Long delete(Long id) {
        String query = "DELETE FROM FILMS WHERE id = ?";
        if (jdbcTemplate.update(query, id) == 0) {
            throw new ResourceNotFoundException("Запись в БД не найдена");
        } else {
            return id;
        }
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "SELECT * FROM \"FILMS\"";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film findById(Long id) {
        String sqlQuery = "SELECT * FROM \"FILMS\" WHERE ID = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (filmRows.next()) {
            Film film = Film.builder()
                    .id(filmRows.getLong("ID"))
                    .name(filmRows.getString("NAME"))
                    .description(filmRows.getString("DESCRIPTION"))
                    .releaseDate(Objects.requireNonNull(filmRows.getDate("RELEASE_DATE")).toLocalDate())
                    .duration(filmRows.getInt("DURATION"))
                    .build();

            film.setLikesUser(new HashSet<>(getLikesOfFilm(film.getId())));
            log.info("Найден фильм с id {}", id);
            return film;
        }
        log.warn("Фильм с id {} не найден", id);
        throw new ResourceNotFoundException("Что то не работает");
    }

    @Override
    public List<Long> getLikesOfFilm(Long filmId) {
        String queryForFilmLikes = "SELECT USER_ID ID FROM FILM_LIKES WHERE FILM_ID = ?;";
        return jdbcTemplate.query(queryForFilmLikes, this::mapRowToLike, filmId);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        try {
            String sqlQuery = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?, ?);";
            jdbcTemplate.update(sqlQuery, filmId, userId);
        } catch (Exception e) {
            log.warn("Лайк фильму с id {} от пользователя с id {} уже существует", filmId, userId);
        }
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?;";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public List<Film> getMostPopular(Integer limit) {
        String sql = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, " +
                "F.DURATION, F.RATING_ID MPA_ID, R.NAME MPA_NAME, COUNT(FL.FILM_ID) RATE " +
                "FROM FILMS F " +
                "LEFT JOIN FILM_LIKES FL ON F.ID = FL.FILM_ID " +
                "LEFT JOIN RATING R ON F.RATING_ID = R.ID " +
                "GROUP BY F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.RATING_ID, R.NAME " +
                "ORDER BY RATE DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, this::mapRowToFilm, limit);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getLong("ID"))
                .name(rs.getString("NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .build();
        log.info("Film = {}", film);
        film.setLikesUser(new HashSet<>(getLikesOfFilm(film.getId())));
        return film;
    }

    private Long mapRowToLike(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("ID");
    }

    @Override
    public List<Film> findCommonFilms(Long userId, Long friendId) {
        String sqlQuery = "SELECT f.ID, f.NAME, f.DESCRIPTION, \n" +
                "f.RELEASE_DATE, f.DURATION, f.RATING_ID AS MPA_ID, \n" +
                "r.name AS mpa_name, \n" +
                "COUNT(fl3.user_id) AS likes\n" +
                "FROM FILMS f \n" +
                "LEFT JOIN RATING r ON f.RATING_ID = r.ID\n" +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id\n" +
                "LEFT JOIN film_likes fl2 ON f.id = fl2.film_id\n" +
                "LEFT JOIN film_likes fl3 ON f.id = fl3.film_id\n" +
                "WHERE fl.user_id = ? AND fl2.user_id = ? \n" +
                "GROUP BY f.ID \n" +
                "ORDER BY likes DESC;";

        return Optional.of(jdbcTemplate.query(sqlQuery, this::mapRowToFilm, userId, friendId))
                .orElse(Collections.emptyList());
    }
}