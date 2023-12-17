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
    @NotNull
    private String description;

    @NotNull
    @DateRelease
    private LocalDate releaseDate;

    @Positive
    @NotNull
    private int duration;


    private final Set<Long> likes = new HashSet<>();


    public int getCountLikes() {

        return likes.size();
    }
}
