package ru.yandex.practicum.filmorate.storage.dao.impl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorage;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAllMpa() {

        String sqlQuery = "SELECT * FROM mpa_ratings ORDER BY rating_id";

        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public Optional<Mpa> getMpaById(Integer mpaId) {

        if (mpaId == null) {
            throw new ValidationException("Был передан пустой аргумент");
        }

        String sqlQuery = "SELECT * FROM mpa_ratings WHERE rating_id=?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sqlQuery, mpaId);

        if (mpaRows.next()) {
            Mpa mpa = new Mpa(mpaRows.getInt("rating_id"), mpaRows.getString("rating_name"));
            log.info("Найден рейтинг с id {}", mpaId);
            return Optional.of(mpa);
        }
        log.info("Рейтинг с id {} не найден", mpaId);
        return Optional.empty();
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("rating_id"), rs.getString("rating_name"));
    }
}
