package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User update(User user);

    User create(User user);

    User delete(Integer userId);

    User getUserById(Integer userId);

    List<User> getAll();
}
