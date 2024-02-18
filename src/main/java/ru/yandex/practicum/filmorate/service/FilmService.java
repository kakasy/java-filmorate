package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

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

    public void addLike(Long filmId, Long userId) {

        filmStorage.addLike(filmId, userId);
    }


    public void deleteLike(Long filmId, Long userId) {

        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopular(Integer count) {

        return filmStorage.getPopular(count);
    }
}
