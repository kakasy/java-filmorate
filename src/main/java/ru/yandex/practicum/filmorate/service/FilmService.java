package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    public Film update(Film film) {

        if (filmStorage.getFilmById(film.getId()).isEmpty()) {
            throw new EntityNotFoundException("Фильм с id=" + film.getId() + " не найден");
        }
        return filmStorage.update(film);
    }

    public Film create(Film film) {

        return filmStorage.create(film);
    }

    public Film delete(Long filmId) {

        return filmStorage.delete(filmId);
    }

    public Film getFilmById(Long filmId) {

        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new EntityNotFoundException("Фильм с id=" + filmId + " не найден"));
    }

    public List<Film> getAll() {

        return filmStorage.getAll();
    }

    public List<Film> getPopular(Integer count) {

        return filmStorage.getPopular(count);
    }

    public void addLike(Long filmId, Long userId) {

        Film film = getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        String sqlQuery = "INSERT INTO films_likes (film_id, user_id) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {

        Film film = getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        String sqlQuery = "DELETE FROM films_likes WHERE film_id=? AND user_id=?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }
}
