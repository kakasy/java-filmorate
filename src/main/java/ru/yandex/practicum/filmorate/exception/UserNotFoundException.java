package ru.yandex.practicum.filmorate.exception;

public class UserNotFoundException extends IllegalArgumentException {

    public UserNotFoundException(String message) {
        super(message);
    }

}
