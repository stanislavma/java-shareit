package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto add(UserDto userDto);

    UserDto update(UserDto userDto);

    void delete(long id);

    UserDto getById(long id);

    List<UserDto> getAll();

}
