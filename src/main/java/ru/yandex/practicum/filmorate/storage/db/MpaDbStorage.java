package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<Long, Mpa> findAll() {
        Map<Long, Mpa> allMpa = new HashMap<>();
        String sqlQuery = "SELECT * FROM RATING;";
        List<Mpa> mpaFromDb = jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
        for (Mpa mpa : mpaFromDb) {
            allMpa.put(mpa.getId(), mpa);
        }
        return allMpa;
    }

    @Override
    public Mpa findById(Long id) {
        String sqlQuery = "SELECT * FROM RATING WHERE ID = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (mpaRows.next()) {
            Mpa mpa = new Mpa(mpaRows.getLong("ID"), mpaRows.getString("NAME"));
            log.info("Найден рейтинг с id {}", id);
            return mpa;
        }
        log.warn("Рейтинг с id {} не найден", id);
        throw new ResourceNotFoundException(String.format("Рейтинг с id %d не найден", id));
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getLong("ID"), rs.getString("NAME"));
    }
}