package ru.yandex.practicum.filmorate.storage.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;

    }

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

        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(queryForFilmGenre, film.getId(), genre.getId());
            }
        }

        return getFilmById(film.getId()).get();
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
        String sqlQueryToDeleteGenres = "DELETE FROM films_genres WHERE film_id=?";
        String sqlQueryUpdateGenres = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?);";

        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getMpa().getId(), film.getDuration(), film.getId());


        jdbcTemplate.update(sqlQueryToDeleteGenres, film.getId());
        if (!film.getGenres().isEmpty()) {

            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlQueryUpdateGenres, film.getId(), genre.getId());
            }
        }

        return getFilmById(film.getId()).get();
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

            List<Genre> genres = getFilmGenres(filmId);
            List<Long> likes = getFilmLikes(film.getId());

            film.getGenres().addAll(genres);
            film.getLikes().addAll(likes);

            log.info("Найден фильм с id {}", filmId);
            return Optional.of(film);
        }
        log.info("Фильм с id {} не найден", filmId);
        return Optional.empty();
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

        List<Genre> genres = getFilmGenres(film.getId());
        List<Long> likes = getFilmLikes(film.getId());

        film.getGenres().addAll(genres);
        film.getLikes().addAll(likes);

        return film;
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {

        return new Genre(rs.getInt("genre_id"), rs.getString("name"));
    }

    private Long mapRowToLike(ResultSet rs, int rowNum) throws SQLException {

        return rs.getLong("user_id");
    }

    private List<Genre> getFilmGenres(Long filmId) {

        String queryForFilmGenres = "SELECT fg.film_id, fg.genre_id, g.name FROM films_genres AS fg" +
                " JOIN genres AS g ON g.genre_id = fg.genre_id WHERE film_id=?";
        return jdbcTemplate.query(queryForFilmGenres, this::mapRowToGenre, filmId);
    }

    private List<Long> getFilmLikes(Long filmId) {

        String queryForFilmLikes = "SELECT user_id FROM films_likes WHERE film_id=?";
        return jdbcTemplate.query(queryForFilmLikes, this::mapRowToLike, filmId);
    }

    @Override
    public void addLike(Long filmId, Long userId) {

        Film film = getFilmById(filmId).get();
        User user = userStorage.getUserById(userId).get();

        String sqlQuery = "INSERT INTO films_likes (film_id, user_id) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {

        Film film = getFilmById(filmId).get();
        User user = userStorage.getUserById(userId).get();

        String sqlQuery = "DELETE FROM films_likes WHERE film_id=? AND user_id=?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public List<Film> getPopular(Integer count) {

        return getAll().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
