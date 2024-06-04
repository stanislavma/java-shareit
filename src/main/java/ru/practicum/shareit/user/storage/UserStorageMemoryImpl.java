package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserStorageMemoryImpl implements UserStorage {

    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    private long userIdCounter = 1;

    @Override
    public User add(User user) {
        if (user.getId() == null) {
            user.setId(userIdCounter++);
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getId() == null) {
            user.setId(userIdCounter++);
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAll() {
        return List.copyOf(users.values());
    }

    @Override
    public User deleteById(Long id) {
        return users.remove(id);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

}