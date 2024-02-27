package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    List<Genre> getGenres();

    Optional<Genre> getGenreById(Integer genreId);

    void load(List<Film> films);

    void updateFilmGenres(Film film);

}