package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    private Long userIdGen = 0L;

    @Override
    public User create(User user) {

        if (users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с таким id уже создан");
        } else {
            Long currId = ++userIdGen;
            user.setId(currId);
            users.put(currId, user);
            users.put(user.getId(), user);

            log.info("Новый пользователь с логином: " + "'" + user.getLogin() + "'" + " создан");

            return user;
        }
    }

    @Override
    public User update(User user) {

        if (user.getId() == null) {

            throw new ValidationException("Аргумент пустой");

        } else if (!users.containsKey(user.getId())) {

            throw new UserNotFoundException("Пользователь с таким id не найден");

        } else {

            users.put(user.getId(), user);
            log.info("Пользователь с id={} обновлен", user.getId());

            return user;
        }
    }

    @Override
    public User delete(Long id) {

        User userToDelete = users.remove(id);

        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь с id=" + id + " не найден");
        }

        for (User user : users.values()) {
            user.getFriends().remove(id);
        }

        return userToDelete;
    }

    @Override
    public Optional<User> getUserById(Long userId) {

        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> getAll() {

        return new ArrayList<>(users.values());
    }
}
