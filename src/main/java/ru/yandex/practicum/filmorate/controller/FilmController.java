package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;


@RestController
@RequestMapping("/films")
@Slf4j
@Validated
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> getAll() {

        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {

        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@Positive @RequestParam(name = "count", defaultValue = "10") Integer count) {

        return filmService.getPopular(count);
    }


    @PostMapping
    public Film create(@Valid @RequestBody Film film) {

        log.info("Получен POST-запрос к эндпоинту: '/films' на добавление фильма");
        return filmService.create(film);

    }


    @PutMapping
    public Film update(@Valid @RequestBody Film film) {

        log.info("Получен PUT-запрос к эндпоинту: '/films' на обновление фильма с id={}", film.getId());
        return filmService.update(film);

    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {

        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {

        filmService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}")
    public Film delete(@PathVariable Long id) {

        log.info("Получен DELETE-запрос к эндпоинту: '/films' на удаление фильма с id={}", id);
        return filmService.delete(id);
    }
}
