package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.impl.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testFindUserById() {
        // Подготавливаем данные для теста

        User user = User.builder()
                .id(1L)
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.create(user);

        // вызываем тестируемый метод
        User savedUser = userStorage.getUserById(1L);

        // проверяем утверждения
        assertThat(savedUser)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(user);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testFindFilmById() {

        Film film = Film.builder()
                .id(1L)
                .name("Пираты Карибского Моря: Проклятие Чёрной жемчужины")
                .description("Жизнь харизматичного авантюриста, капитана Джека Воробья, " +
                        "полная увлекательных приключений, резко меняется, " +
                        "когда его заклятый враг капитан Барбосса похищает корабль Джека Черную Жемчужину...")
                .releaseDate(LocalDate.of(2003,6,28))
                .duration(203)
                .mpa(new Mpa(3, "PG-13"))
                .build();

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);

        filmDbStorage.create(film);

        Film savedFilm = filmDbStorage.getFilmById(1L).get();

        assertThat(savedFilm)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(film);        // и сохраненного пользователя - совпадают
    }
}
