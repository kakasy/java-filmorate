package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    private static int userIdGen = 0;

    @Override
    public User create(User user) {

        if (users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с таким id уже создан");
        }

        if (isValidUser(user)) {

            int currId = ++userIdGen;
            user.setId(currId);
            users.put(currId, user);
            users.put(user.getId(), user);

            log.info("Новый пользователь с логином: " + "'" + user.getLogin() + "'" + " создан");

        }

        return user;
    }

    @Override
    public User update(User user) {

        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с таким id не найден");
        }

        if (isValidUser(user)) {
            users.put(user.getId(), user);
            log.info("Пользователь с id={} обновлен", user.getId());
        }

        return user;
    }

    @Override
    public User delete(Integer id) {

        User userToDelete = users.remove(id);

        if (userToDelete == null) {
            throw new ValidationException("Пользователя с таким id не существует");
        }
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь с id=" + id + " не найден");
        }

        for (User user : users.values()) {
            user.getFriends().remove(id);
        }

        return userToDelete;
    }

    @Override
    public User getUserById(Integer userId) {

        User user = users.get(userId);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        }
        return user;
    }

    @Override
    public List<User> getAll() {

        return new ArrayList<>(users.values());
    }


    private boolean isValidUser(User user) {
        if (user.getEmail().isEmpty()) {
            throw new ValidationException("Email не должен быть пустым");
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен быть пустым/содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не должна быть в будущем");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return true;
    }
}
