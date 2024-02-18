package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public List<Genre> getGenres() {

        return genreStorage.getGenres();
    }

    public Genre getGenreById(Integer genreId) {

        return genreStorage.getGenreById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Жанр с id=" + genreId + " не найден"));
    }

}
