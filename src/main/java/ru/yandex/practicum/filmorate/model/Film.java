package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validator.DateRelease;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */

@Data
@Builder
public class Film {

    private Long id;

    @NotEmpty(message = "Название фильма не может быть пустым")
    private String name;

    @Length(message = "Максимальная длина названия - 200 символов", max = 200)
    private String description;

    @NotNull
    @DateRelease
    private LocalDate releaseDate;

    @Positive
    @NotNull
    private int duration;

    private Set<Long> likes;

    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration, Set<Long> likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = likes;
        if (likes == null) {
            this.likes = new HashSet<>();
        }
    }
}
