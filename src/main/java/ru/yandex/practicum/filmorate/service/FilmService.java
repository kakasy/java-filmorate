package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

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

        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с id=" + filmId + " не найден"));
    }

    public List<Film> getAll() {

        return filmStorage.getAll();
    }

    public void addLike(Long filmId, Long userId) {

        Film film = getFilmById(filmId);
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с id=" + userId + " не существует"));

        film.getLikes().add(user.getId());
    }


    public void deleteLike(Long filmId, Long userId) {

        Film film = getFilmById(filmId);
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с id=" + userId + " не существует"));

        film.getLikes().remove(user.getId());
    }

    public List<Film> getPopular(Integer count) {

        return filmStorage.getAll().stream()
                .sorted((film1, film2) -> film2.getCountLikes() - film1.getCountLikes())
                .limit(count)
                .collect(Collectors.toList());
    }
}
