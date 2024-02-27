package ru.yandex.practicum.filmorate.storage.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getAll() {

        String sqlQuery = "SELECT f.*, mr.rating_name FROM films AS f JOIN mpa_ratings AS mr ON f.rating_id = mr.rating_id";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film create(Film film) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?);";
        String queryForFilmGenre = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?);";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt =
                    connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
//
//        if (!film.getGenres().isEmpty()) {
//            List<Genre> genres =  new ArrayList<>(film.getGenres());
//            updateBatchFilmGenres(queryForFilmGenre, film, genres);
//        }


        return film;
    }

    @Override
    public Film delete(Long filmId) {

        Film film = getFilmById(filmId).get();
        String sqlQuery = "DELETE FROM films WHERE film_id=?";
        jdbcTemplate.update(sqlQuery, filmId);
        return film;
    }

    @Override
    public Film update(Film film) {

        String sqlQuery = "UPDATE films SET name=?, description=?, release_date=?, rating_id=?, duration=?" +
                " WHERE film_id=?";
        String sqlQueryToDeleteGenres = "DELETE FROM films_genres WHERE film_id=?"; // в жанры
        String sqlQueryUpdateGenres = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?);";

        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getMpa().getId(), film.getDuration(), film.getId());

        jdbcTemplate.update(sqlQueryToDeleteGenres, film.getId());
//        if (!film.getGenres().isEmpty()) {
//            List<Genre> genres = new ArrayList<>(film.getGenres());
//            updateBatchFilmGenres(sqlQueryUpdateGenres, film, genres);
//        }

        return film;
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {

        String sqlQuery = "SELECT f.*, mr.rating_name FROM films AS f JOIN mpa_ratings AS mr " +
                "ON f.rating_id = mr.rating_id WHERE film_id=?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, filmId);

        if (filmRows.next()) {
            Film film = Film.builder()
                    .id(filmRows.getLong("film_id"))
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(Objects.requireNonNull(filmRows.getDate("release_date")).toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .mpa(new Mpa(filmRows.getInt("rating_id"), filmRows.getString("rating_name")))
                    .build();

//            List<Genre> genres = getFilmGenres(filmId);
//
//            film.getGenres().addAll(genres);

            log.info("Найден фильм с id {}", filmId);
            return Optional.of(film);
        }
        log.info("Фильм с id {} не найден", filmId);
        return Optional.empty();
    }

    @Override
    public List<Film> getPopular(Integer count) {

        String sqlQuery = "SELECT f.*, mr.rating_name, fl.user_id FROM films AS f JOIN mpa_ratings AS mr " +
                "ON f.rating_id = mr.rating_id " +
                "LEFT JOIN films_likes AS fl ON f.film_id = fl.film_id " +
                "GROUP BY f.film_id ORDER BY COUNT(fl.user_id) DESC LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {

        Film film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("rating_id"), rs.getString("rating_name")))
                .build();

//        List<Genre> genres = getFilmGenres(film.getId());
//
//        film.getGenres().addAll(genres);

        return film;
    }

//    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
//
//        return new Genre(rs.getInt("genre_id"), rs.getString("name"));
//    }

//    private List<Genre> getFilmGenres(Long filmId) {
//
//        String queryForFilmGenres = "SELECT fg.film_id, fg.genre_id, g.name FROM films_genres AS fg" +
//                " JOIN genres AS g ON g.genre_id = fg.genre_id WHERE film_id=?";
//        return jdbcTemplate.query(queryForFilmGenres, this::mapRowToGenre, filmId);
//    }

    private void updateBatchFilmGenres(String sql, Film film, List<Genre> genres) {

        jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, film.getId());
                        ps.setInt(2, genres.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return film.getGenres().size();
                    }
                });
    }
}

