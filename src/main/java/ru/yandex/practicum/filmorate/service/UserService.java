package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public User create(User user) {
        fillEmptyName(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        if (userStorage.findById(user.getId()) == null) {
            throw new ResourceNotFoundException("Ошибка! Невозможно обновить пользователя - его не существует.");
        }
        fillEmptyName(user);
        return userStorage.update(user);
    }

    public Long removeById(Long id) {
        return userStorage.delete(id);
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Long id) {
        User user = userStorage.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        return userStorage.findById(id);
    }

    public User addFriend(Long id, Long friendId) {
        if (userStorage.findById(id) == null || userStorage.findById(friendId) == null) {
            throw new ResourceNotFoundException("Не найден пользователь");
        }
        userStorage.addFriend(id, friendId);
        return userStorage.findById(id);
    }

    public User removeFriend(Long id, Long friendId) {
        if (userStorage.findById(id) == null || userStorage.findById(friendId) == null) {
            throw new ResourceNotFoundException("Не найден пользователь");
        }
        userStorage.removeFriend(id, friendId);
        return userStorage.findById(id);
    }

    public List<User> findAllFriends(Long id) {
        if (userStorage.findById(id) == null) {
            throw new ResourceNotFoundException("Не найден пользователь");
        }
        return userStorage.findAllFriends(id);
    }

    public List<User> findCommonFriends(Long id, Long friendId) {
        if (userStorage.findById(id) == null || userStorage.findById(friendId) == null) {
            throw new ResourceNotFoundException("Не найден пользователь");
        }

        Set<User> friends = new HashSet<>(userStorage.findAllFriends(id));
        Set<User> otherFriends = new HashSet<>(userStorage.findAllFriends(friendId));

        if (friends.isEmpty() || otherFriends.isEmpty()) {
            return new ArrayList<>();
        }

        return friends.stream()
                .filter(otherFriends::contains)
                .collect(Collectors.toList());
    }

    private void fillEmptyName(User user) {
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}