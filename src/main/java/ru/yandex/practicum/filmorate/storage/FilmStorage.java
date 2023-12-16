package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film update(Film film);

    Film create(Film film);

    Film delete(Long filmId);

    Film getFilmById(Long filmId);

    List<Film> getAll();
}
