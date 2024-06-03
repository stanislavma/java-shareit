package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserStorageMemoryImpl implements UserStorage {

    private final ConcurrentHashMap<Long, UserDto> users = new ConcurrentHashMap<>();
    private long userIdCounter = 1;

    @Override
    public UserDto add(UserDto user) {
        if (user.getId() == null) {
            user.setId(userIdCounter++);
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public UserDto update(UserDto user) {
        if (user.getId() == null) {
            user.setId(userIdCounter++);
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<UserDto> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<UserDto> getAll() {
        return List.copyOf(users.values());
    }

    @Override
    public UserDto deleteById(Long id) {
       return users.remove(id);
    }

    @Override
    public Optional<UserDto> getByEmail(String email) {
        return users.values().stream().filter(user -> user.getEmail().equals(email)).findFirst();
    }

}