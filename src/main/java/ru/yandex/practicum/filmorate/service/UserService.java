package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {

        this.userStorage = userStorage;
    }

    public User update(User user) {

        return userStorage.update(user);
    }

    public User create(User user) {

        return userStorage.create(user);
    }

    public User delete(Long userId) {

        return  userStorage.delete(userId);
    }

    public User getUserById(Long userId) {

        return userStorage.getUserById(userId);
    }

    public List<User> getAll() {

        return userStorage.getAll();
    }

    public void addFriend(Long userId, Long friendId) {

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

    }

    public void deleteFriend(Long userId, Long friendId) {

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (user.getFriends().contains(friendId)) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
        }
    }

    public List<User> getFriends(Long userId) {

        User user = userStorage.getUserById(userId);
        List<User> friends = new ArrayList<>();

        if (user.getFriends() != null) {
            for (Long id : user.getFriends()) {
                friends.add(userStorage.getUserById(id));
            }
        }

        return friends;
    }

    public List<User> getCommonFriends(Long userId, Long anotherUserId) {

        User user = userStorage.getUserById(userId);
        User anotherUser = userStorage.getUserById(anotherUserId);

        Set<Long> commonFriendsId = new HashSet<>(user.getFriends());

        commonFriendsId.retainAll(anotherUser.getFriends());

        List<User> commonFriends = new ArrayList<>();

        for (Long id : commonFriendsId) {
            commonFriends.add(userStorage.getUserById(id));
        }

        return commonFriends;
    }
}
