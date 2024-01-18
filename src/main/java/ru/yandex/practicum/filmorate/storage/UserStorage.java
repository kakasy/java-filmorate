package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User update(User user);

    User create(User user);

    User delete(Long userId);

    Optional<User> getUserById(Long userId);

    List<User> getAll();
}
