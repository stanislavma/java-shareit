package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User add(User user);

    User update(User user);

    Optional<User> getById(Long id);

    List<User> getAll();

    User deleteById(Long id);

    Optional<User> getByEmail(String email);

}