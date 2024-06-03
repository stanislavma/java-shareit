package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserDto add(UserDto userDto);

    UserDto update(UserDto userDto);

    UserDto delete(long id);

    UserDto getById(long id);

    List<UserDto> getAll();

}
