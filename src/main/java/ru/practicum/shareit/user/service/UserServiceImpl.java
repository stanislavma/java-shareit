package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto add(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new ValidationException("Email обязательное поле!", HttpStatus.BAD_REQUEST);
        }

        if (userStorage.getByEmail(userDto.getEmail()).isPresent()) {
            throw new ValidationException("Email уже существует в системе!", HttpStatus.CONFLICT);
        }

        return userStorage.add(userDto);
    }

    @Override
    public UserDto update(UserDto userDto) {
        UserDto existingUser = userStorage.getById(userDto.getId())
                .orElseThrow(() -> {
                    String errorText = "Пользователь не найден: " + userDto.getId();
                    log.error(errorText);
                    return new EntityNotFoundException(errorText);
                });

        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            if (userStorage.getByEmail(userDto.getEmail()).isPresent()) {
                throw new ValidationException("Email уже имеется в системе", HttpStatus.CONFLICT);
            }
        }

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }

        return userStorage.update(existingUser);
    }

    @Override
    public UserDto getById(long userId) {
        return userStorage.getById(userId)
                .orElseThrow(() -> {
                    String errorText = "Пользователь не найден: " + userId;
                    log.error(errorText);
                    return new EntityNotFoundException(errorText);
                });
    }

    @Override
    public UserDto delete(long userId) {
        if (!userStorage.getById(userId).isPresent()) {
            throw new EntityNotFoundException("Email не найден: " + userId);
        }

        return userStorage.deleteById(userId);
    }

    @Override
    public List<UserDto> getAll() {
        return userStorage.getAll();
    }

}