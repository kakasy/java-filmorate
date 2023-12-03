package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import javax.validation.Valid;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    private static int filmIdGen = 0;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {

        log.info("POST-запрос на эндпоинт /films");

        if (films.containsKey(film.getId())) {
            throw new ValidationException("Фильм с таким id уже создан");
        }

        if (isValidFilm(film)) {

            int currId = ++filmIdGen;
            film.setId(currId);
            films.put(currId, film);
            log.info("Новый фильм с названием: " + film.getName() + " создан");
        }
        return film;

    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {

        log.info("PUT-запрос на обновление фильма с id={}", film.getId());

        if (!films.containsKey(film.getId())) {
            create(film);
        }

        if (isValidFilm(film)) {
            films.put(film.getId(), film);
            log.info("Фильм с id={} обновлен", film.getId());
        }

        return film;
    }

    @GetMapping
    public List<Film> getAll() {

        return new ArrayList<>(films.values());
    }

    private boolean isValidFilm(Film film) {
        if (film.getName().isEmpty()) {
            throw new ValidationException("Название фильма не должно быть пустым");
        }
        if ((film.getDescription().length()) > 200 || (film.getDescription().isEmpty())) {
            throw new ValidationException("Описание фильма больше 200 символов или пустое ");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Некорректная дата релиза фильма: " + film.getReleaseDate());
        }
        if (film.getDuration().isZero()) {
            throw new ValidationException("Продолжительность должна быть положительной");
        }
        return true;
    }
}
