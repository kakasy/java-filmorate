package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTest {

    private FilmController filmController;
    private Film film;

    @BeforeEach
    public void setUp() {

        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        filmController = new FilmController(new FilmService(filmStorage, userStorage));

        film = Film.builder()
                .name("Пираты Карибского Моря: Проклятие Чёрной жемчужины")
                .description("Жизнь харизматичного авантюриста, капитана Джека Воробья, " +
                        "полная увлекательных приключений, резко меняется, " +
                        "когда его заклятый враг капитан Барбосса похищает корабль Джека Черную Жемчужину...")
                .releaseDate(LocalDate.of(2003,6,28))
                .duration(203)
                .build();

    }

    @Test
    void testShouldCreateFilmWhenAllFieldsAreCorrect() {
        Film pirates = filmController.create(film);
        assertEquals(film, pirates);
        assertEquals(1, filmController.getAll().size(), "Размер списка фильмов должен быть равен 1");
    }

    // при пустом названии должна быть выброшена ошибка валидации
    @Test
    void testShouldNotCreateFilmWhenNameIsEmpty() {
        film.setName("");
        assertEquals(0, filmController.getAll().size(), "Список фильмов должен быть пустым");
    }

    // должна быть выброшена ошибка валидации при длине описания > 200 символов
    @Test
    void testShouldNotCreateFilmWhenDescriptionIsGreaterThan200Symbols() {
        film.setDescription(film.getDescription() + film.getDescription());
        assertEquals(0, filmController.getAll().size(), "Размер списка фильмов должен быть равен 0");
    }

    // должна быть выброшена ошибка валидации при пустом описании фильма
    @Test
    void testShouldNotCreateFilmWhenFilmDescriptionIsEmpty() {
        film.setDescription("");
        assertEquals(0, filmController.getAll().size(), "Размер списка фильмов должен быть равен 0");
    }

    // должна быть выброшена ошибка валидации если дата релиза фильма раньше 28.12.1895
    @Test
    void testShouldNotCreateFilmWhenFilmReleaseDateIsBefore28121895() {
        film.setReleaseDate(LocalDate.of(1895,1,1));
        assertEquals(0, filmController.getAll().size(), "Размер списка фильмов должен быть равен 0");
    }

    // должна быть выброшена ошибка валидации, когда длительность фильма равна нулю
    @Test
    void testShouldNotCreateFilmWhenFilmDurationIsZero() {
        film.setDuration(0);
        assertEquals(0, filmController.getAll().size(), "Размер списка фильмов должен быть равен 0");
    }

}
