package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAll() {

        return userService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {

        return Optional.of(userService.getUserById(id));
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {

        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {

        return userService.getMutualFriends(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {

        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {

        userService.deleteFriend(id, friendId);
    }

    @ResponseBody
    @PostMapping
    public User create(@Valid @RequestBody User user) {

        log.info("Получен POST-запрос к эндпоинту: '/users' на создание пользователя");
        return userService.create(user);
    }

    @ResponseBody
    @PutMapping
    public User update(@Valid @RequestBody User user) {

        log.info("Получен PUT-запрос к эндпоинту: '/users' на обновление пользователя с id={}", user.getId());
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {

        log.info("Получен DELETE-запрос к эндпоинту: '/users' на удаление пользователя с id={}", id);
        userService.delete(id);
    }
}
