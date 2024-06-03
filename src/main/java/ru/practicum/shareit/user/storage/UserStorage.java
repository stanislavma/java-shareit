package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    UserDto add(UserDto user);

    UserDto update(UserDto user);

    Optional<UserDto> getById(Long id);

    List<UserDto> getAll();

    UserDto deleteById(Long id);

    Optional<UserDto> getByEmail(String email);

}