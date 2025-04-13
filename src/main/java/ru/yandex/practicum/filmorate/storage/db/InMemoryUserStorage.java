package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> friends = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public User create(User user) {
        user.setId(idCounter.getAndIncrement());
        users.put(user.getId(), user);
        friends.put(user.getId(), new HashSet<>());
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Long delete(Long id) {
        if (!users.containsKey(id)) {
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        users.remove(id);
        friends.remove(id);
        // Удаляем из друзей у других пользователей
        friends.values().forEach(friendSet -> friendSet.remove(id));
        return id;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        return user;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        validateUserIds(userId, friendId);
        friends.get(userId).add(friendId);
        friends.get(friendId).add(userId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        validateUserIds(userId, friendId);
        friends.get(userId).remove(friendId);
        friends.get(friendId).remove(userId);
    }

    @Override
    public List<User> findAllFriends(Long userId) {
        if (!friends.containsKey(userId)) {
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        return friends.get(userId).stream()
                .map(this::findById)
                .toList();
    }

    public List<User> findCommonFriends(Long userId, Long otherUserId) {
        validateUserIds(userId, otherUserId);

        Set<Long> commonFriendsIds = new HashSet<>(friends.get(userId));
        commonFriendsIds.retainAll(friends.get(otherUserId));

        return commonFriendsIds.stream()
                .map(this::findById)
                .toList();
    }

    private void validateUserIds(Long userId, Long friendId) {
        if (!users.containsKey(userId) || !users.containsKey(friendId)) {
            throw new ResourceNotFoundException("Пользователь не найден");
        }
    }
}