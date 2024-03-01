package ru.yandex.practicum.filmorate.exception;

public class EntityNotFoundException extends IllegalArgumentException {

    public EntityNotFoundException(String message) {

        super(message);
    }

}