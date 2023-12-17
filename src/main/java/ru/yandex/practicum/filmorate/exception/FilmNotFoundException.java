package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends IllegalArgumentException {

    public FilmNotFoundException(String message) {
        super(message);
    }

}
