package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@Builder
public class User {

    private Long id;

    @NotEmpty
    @Email(message = "Введён некорректный адрес")
    private String email;

    @NotEmpty(message = "Логин не может быть пустым или содержать пробелы")
    @Pattern(regexp = "^\\S+$")
    private String login;
    private String name;

    @NotNull
    @PastOrPresent(message = "День рождения не должен быть в будущем")
    private LocalDate birthday;


    private final Set<Long> friends = new HashSet<>();

}
