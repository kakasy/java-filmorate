package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }


    public Film update(Film film) {

       return filmStorage.update(film);
    }

    public Film create(Film film) {

        return filmStorage.create(film);
    }

    public Film delete(Long filmId) {

        return filmStorage.delete(filmId);
    }

    public Film getFilmById(Long filmId) {

        return filmStorage.getFilmById(filmId);
    }

    public List<Film> getAll() {

        return filmStorage.getAll();
    }

    public void addLike(Long filmId, Long userId) {

        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        if (film != null) {
            if (user != null) {
                film.getLikes().add(userId);
            } else {
                throw new UserNotFoundException("Пользователя с id=" + userId + " не существует");
            }
        } else {
            throw new FilmNotFoundException("Фильма с id=" + filmId + " не существует");
        }
    }


    public void deleteLike(Long filmId, Long userId) {

        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        if (film != null && user != null) {

            if (film.getLikes().contains(userId)) {
                film.getLikes().remove(userId);
            } else {
                throw new UserNotFoundException("Пользователь с id=" + userId + " не ставил лайк");
            }
        } else {
            throw new FilmNotFoundException("Фильма с указанным id=" + filmId + " не существует");
        }
    }

    public List<Film> getPopular(Integer count) {

        if (count < 1) {
            throw new ValidationException("Количество фильмов не может быть меньше 1");
        }

        return filmStorage.getAll().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
