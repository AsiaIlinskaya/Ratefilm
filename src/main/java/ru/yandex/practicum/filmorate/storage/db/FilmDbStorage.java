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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        if (!film.getGenres().isEmpty()) {
            checkGenresExist(film.getGenres());
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) " +
                "VALUES (?, ?, ?, ?, ?);";
        String queryForFilmGenre = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?);";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        if (!film.getGenres().isEmpty()) {
            Set<Genre> uniqueGenres = new HashSet<>(film.getGenres());
            for (Genre genre : uniqueGenres) {
                jdbcTemplate.update(queryForFilmGenre, film.getId(), genre.getId());
            }
        }
        return findById(film.getId());
    }

    private void checkGenresExist(Set<Genre> genres) {
        String sql = "SELECT ID FROM GENRE WHERE ID IN (%s)";
        String inClause = String.join(",", Collections.nCopies(genres.size(), "?"));

        List<Long> existingGenreIds = jdbcTemplate.queryForList(
                String.format(sql, inClause),
                Long.class,
                genres.stream().map(Genre::getId).toArray()
        );

        if (existingGenreIds.size() != genres.size()) {
            Set<Long> receivedIds = genres.stream().map(Genre::getId).collect(Collectors.toSet());
            receivedIds.removeAll(existingGenreIds);
            throw new ResourceNotFoundException(String.format("Жанры с id %s не найдены", receivedIds));
        }
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, RATING_ID = ?, DURATION = ?" +
                " WHERE ID = ?;";
        String queryToDeleteFilmGenres = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?;";
        String queryForUpdateGenre = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getMpa().getId(), film.getDuration(), film.getId());
        jdbcTemplate.update(queryToDeleteFilmGenres, film.getId());
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(queryForUpdateGenre, film.getId(), genre.getId());
            }
        }
        return findById(film.getId());
    }

    @Override
    public Long delete(Long id) {
        String query = "DELETE FROM FILMS WHERE id = ?";
        if (jdbcTemplate.update(query, id) == 0) {
            throw new ResourceNotFoundException(String.format("Фильм с id %d не найден", id));
        } else {
            return id;
        }
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "SELECT F.*, R.ID MPA_ID, R.NAME MPA_NAME FROM FILMS AS F JOIN RATING AS R ON F.RATING_ID = R.ID ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film findById(Long id) {
        String sqlQuery = "SELECT F.*, R.ID MPA_ID, R.NAME MPA_NAME FROM FILMS AS F JOIN RATING AS R ON F.RATING_ID = R.ID " +
                " WHERE F.ID = ?;";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (filmRows.next()) {
            Film film = Film.builder()
                    .id(filmRows.getLong("ID"))
                    .name(filmRows.getString("NAME"))
                    .description(filmRows.getString("DESCRIPTION"))
                    .releaseDate(Objects.requireNonNull(filmRows.getDate("RELEASE_DATE")).toLocalDate())
                    .duration(filmRows.getInt("DURATION"))
                    .mpa(new Mpa(filmRows.getLong("MPA_ID"), filmRows.getString("MPA_NAME")))
                    .build();

            film.setGenres(getGenresOfFilm(id).stream().sorted(Comparator.comparingLong(Genre::getId))
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
            film.setLikesUser(new HashSet<>(getLikesOfFilm(film.getId())));


            log.info("Найден фильм с id {}", id);
            return film;
        }
        log.warn("Фильм с id {} не найден", id);
        throw new ResourceNotFoundException(String.format("Фильм с id %d не найден", id));
    }

    private List<Genre> getGenresOfFilm(Long filmId) {
        String queryForFilmGenres = "SELECT FG.FILM_ID, FG.GENRE_ID, G.NAME FROM FILM_GENRE FG" +
                " JOIN GENRE G ON G.ID = FG.GENRE_ID WHERE FILM_ID = ?;";
        return jdbcTemplate.query(queryForFilmGenres, this::mapRowToGenre, filmId);
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
        log.info("Film build start>>>>>");
        Film film = Film.builder()
                .id(rs.getLong("ID"))
                .name(rs.getString("NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .mpa(new Mpa(rs.getLong("MPA_ID"), rs.getString("MPA_NAME")))
                .build();
        log.info("Film = {}", film);

        film.setGenres(getGenresOfFilm(film.getId()).stream().sorted(Comparator.comparingLong(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new)));

        film.setLikesUser(new HashSet<>(getLikesOfFilm(film.getId())));
        return film;
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getLong("GENRE_ID"), rs.getString("NAME"));
    }

    private Long mapRowToLike(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("ID");
    }
}