package ru.yandex.practicum.filmorate.storage.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserStorage;


import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAll() {

        String sqlQuery = "SELECT * FROM users";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement stmt =
                            connection.prepareStatement(sqlQuery, new String[]{"user_id"});
                    stmt.setString(1, user.getEmail());
                    stmt.setString(2, user.getLogin());
                    stmt.setString(3, user.getName());
                    stmt.setDate(4, Date.valueOf(user.getBirthday()));
                    return stmt;
                }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return getUserById(user.getId()).get();

    }

    @Override
    public User update(User user) {

        String sqlQuery = "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE user_id=?";

        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(),
                user.getId());
        return getUserById(user.getId()).get();

    }

    @Override
    public Optional<User> getUserById(Long userId) {

        if (userId == null) {
            throw new ValidationException("Был передан пустой аргумент");
        }

        String sqlQuery = "SELECT * FROM users WHERE user_id=?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, userId);

        if (userRows.next()) {
            User user = User.builder()
                    .id(userRows.getLong("user_id"))
                    .email(userRows.getString("email"))
                    .login(userRows.getString("login"))
                    .name(userRows.getString("name"))
                    .birthday((Objects.requireNonNull(userRows.getDate("birthday"))).toLocalDate())
                    .build();
            log.info("Найден пользователь с id {}", userId);
            return Optional.of(user);
        }
        log.info("Пользователь с id {} не найден", userId);
        throw new EntityNotFoundException("Пользователь с id=" + userId + " не найден");
    }

    @Override
    public void addFriend(Long userId, Long friendId) {

        User user = getUserById(userId).get();
        User friend = getUserById(friendId).get();
        String sqlQuery = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {

        String sqlQuery = "DELETE FROM friends WHERE user_id=? AND friend_id=?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> getMutualFriends(Long userId, Long otherUserId) {

        String sqlQuery = "SELECT * FROM users AS u WHERE u.user_id IN (SELECT f.friend_id " +
                "FROM friends AS f WHERE f.user_id=? " +
                "INTERSECT SELECT f.friend_id FROM friends AS f WHERE f.user_id=?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, otherUserId);
    }

    @Override
    public List<User> getFriends(Long userId) {

        User user = getUserById(userId).get();
        String sqlQuery = "SELECT * FROM users AS u WHERE u.user_id IN " +
                "(SELECT f.friend_id FROM friends AS f WHERE f.user_id=?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
    }

    @Override
    public User delete(Long userId) {

        User user = getUserById(userId).get();
        String sqlQuery = "DELETE FROM users WHERE user_id=?";
        jdbcTemplate.update(sqlQuery, userId);
        return user;
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {

        return User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday((rs.getDate("birthday")).toLocalDate())
                .build();
    }
}

