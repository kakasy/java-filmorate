package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film update(Film film);

    Film create(Film film);

    Film delete(Long filmId);

    Optional<Film> getFilmById(Long filmId);

    List<Film> getAll();

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Film> getPopular(Integer count);

}
