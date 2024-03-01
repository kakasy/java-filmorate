package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User update(User user) {

        if (userStorage.getUserById(user.getId()) == null) {
            throw new EntityNotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }

        checkUserName(user);
        return userStorage.update(user);
    }

    public User create(User user) {

        checkUserName(user);
        return userStorage.create(user);
    }

    public void delete(Long userId) {

        userStorage.delete(userId);
    }

    public User getUserById(Long userId) {

        return userStorage.getUserById(userId);
    }

    public List<User> getAll() {

        return userStorage.getAll();
    }

    public void addFriend(Long userId, Long friendId) {

        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {

        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {

        return userStorage.getFriends(userId);
    }

    public List<User> getMutualFriends(Long userId, Long anotherUserId) {

       return userStorage.getMutualFriends(userId, anotherUserId);
    }


    private void checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
