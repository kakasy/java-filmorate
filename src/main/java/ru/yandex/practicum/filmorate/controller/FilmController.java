package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {

        log.info("POST-запрос на эндпоинт /films");

        return filmStorage.create(film);

    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {

        log.info("PUT-запрос на обновление фильма с id={}", film.getId());

        return filmStorage.update(film);
    }

    @GetMapping
    public List<Film> getAll() {

        return filmStorage.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {

        return filmStorage.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(name = "count", defaultValue = "10") Integer count) {
        return filmService.getPopular(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {

        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {

        filmService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}")
    public Film delete(@PathVariable Integer id) {

        log.info("Получен DELETE-запрос к эндпоинту: '/films' на удаление фильма с ID={}", id);

        return filmStorage.delete(id);
    }
}
