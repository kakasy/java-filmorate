package ru.yandex.practicum.filmorate.storage.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Override
    public void getFilmGenres(List<Film> films) {

        final Map<Long, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));

        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));

        final String queryForFilmGenres = "SELECT fg.film_id, fg.genre_id, g.name FROM films_genres AS fg " +
                "JOIN genres AS g ON g.genre_id = fg.genre_id WHERE fg.genre_id = g.genre_id AND fg.film_id IN (" + inSql + ")";

        jdbcTemplate.query(queryForFilmGenres, (rs, rowNum) -> {
            final Film film = filmById.get(rs.getLong("film_id"));
            film.getGenres().add(new Genre(rs.getInt("genre_id"), rs.getString("name")));
            return film;
        }, films.stream().map(Film::getId).toArray());
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("name"));
    }
}
