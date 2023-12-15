package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film update(Film film);

    Film create(Film film);

    Film delete(Integer filmId);

    Film getFilmById(Integer filmId);

    List<Film> getAll();
}
