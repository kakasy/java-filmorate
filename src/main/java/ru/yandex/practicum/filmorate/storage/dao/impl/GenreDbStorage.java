package ru.yandex.practicum.filmorate.storage.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getGenres() {

        String sqlQuery = "SELECT * FROM genres ORDER BY genre_id";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Optional<Genre> getGenreById(Integer genreId) {

        String sqlQuery = "SELECT * FROM genres WHERE genre_id=?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, genreId);

        if (genreRows.next()) {
            Genre genre = new Genre(genreRows.getInt("genre_id"),
                    genreRows.getString("name"));
            log.info("Найден жанр с id {}", genreId);
            return Optional.of(genre);
        }
        log.info("Жанр с id {} не найден", genreId);
        return Optional.empty();
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("name"));
    }
}
