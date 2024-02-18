package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    private Long filmIdGen = 0L;

    @Override
    public Film update(Film film) {

        if (!films.containsKey(film.getId())) {
            throw new EntityNotFoundException("Фильм с таким id=" + film.getId() + " не найден");
        }

        films.put(film.getId(), film);
        log.info("Фильм с id={} обновлен", film.getId());

        return film;
    }

    @Override
    public Film create(Film film) {

        if (films.containsKey(film.getId())) {
            throw new ValidationException("Фильм с таким id уже создан");
        }

        Long currId = ++filmIdGen;
        film.setId(currId);
        films.put(currId, film);
        log.info("Новый фильм с названием: " + film.getName() + " создан");

        return film;
    }

    @Override
    public Film delete(Long id) {

        Film filmToDelete = films.remove(id);

        if (filmToDelete == null) {
            throw new ValidationException("Фильма с таким id не существует");
        }

        return filmToDelete;

    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {

        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public List<Film> getAll() {

        return new ArrayList<>(films.values());
    }

    @Override
    public void addLike(Long filmId, Long userId) {

    }

    @Override
    public void deleteLike(Long filmId, Long userId) {

    }

    @Override
    public List<Film> getPopular(Integer count) {
        return null;
    }
}
