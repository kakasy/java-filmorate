package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    public UserController(UserStorage userStorage, UserService userService) {

        this.userStorage = userStorage;
        this.userService = userService;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {

        log.info("POST-запрос на эндпоинт /users");

        return userStorage.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {

        log.info("PUT-запрос на обновление пользователя с id={}", user.getId());

        return userStorage.update(user);
    }

    @GetMapping
    public List<User> getAll() {

        return userStorage.getAll();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {

        userService.addFriend(id, friendId);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {

        log.info("GET-запрос на эндпоинт /users/{id}");

        return userStorage.getUserById(id);

    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable Integer id) {

        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {

        return userService.getCommonFriends(id, otherId);
    }

    @DeleteMapping("/user/{userId}")
    public User delete(@PathVariable Integer userId) {

        return userStorage.delete(userId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {

        userService.deleteFriend(id, friendId);
    }
}
