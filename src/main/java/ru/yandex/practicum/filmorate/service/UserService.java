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

    public void addFriend(Integer userId, Integer friendId) {

        User user = userStorage.getUserById(userId);
        User friendToAdd = userStorage.getUserById(friendId);

        user.getFriends().add(friendId);
        friendToAdd.getFriends().add(userId);

    }

    public void deleteFriend(Integer userId, Integer friendId) {

        User user = userStorage.getUserById(userId);
        User friendToDelete = userStorage.getUserById(friendId);

        if (user.getFriends().contains(friendId)) {
            user.getFriends().remove(friendId);
            friendToDelete.getFriends().remove(userId);
        }
    }

    public List<User> getFriends(Integer userId) {

        User user = userStorage.getUserById(userId);
        List<User> userFriends = new ArrayList<>();

        if (user.getFriends().size() != 0) {
            for (Integer id : user.getFriends()) {
                userFriends.add(userStorage.getUserById(id));
            }
        }

        return userFriends;

    }

    public List<User> getCommonFriends(Integer userId, Integer anotherUserId) {

        User user = userStorage.getUserById(userId);
        User anotherUser = userStorage.getUserById(anotherUserId);

        Set<Integer> commonFriendsId = new HashSet<>(user.getFriends());

        commonFriendsId.retainAll(anotherUser.getFriends());

        List<User> commonFriends = new ArrayList<>();

        for (Integer id : commonFriendsId) {
            commonFriends.add(userStorage.getUserById(id));
        }

        return commonFriends;
    }
}
