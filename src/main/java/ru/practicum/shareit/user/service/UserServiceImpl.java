package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto add(UserDto userDto) {
        return UserMapper.toDto(userRepository.save(UserMapper.toEntity(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto) {
        UserDto existingUser = getById(userDto.getId());

        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            validateIsEmailExist(userDto);
        }

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }

        return UserMapper.toDto(userRepository.saveAndFlush(UserMapper.toEntity(existingUser)));
    }

    private void validateIsEmailExist(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ValidationException("Email уже имеется в системе", HttpStatus.CONFLICT);
        }
    }

    @Override
    public UserDto getById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    String errorText = "Пользователь не найден: " + userId;
                    log.error(errorText);
                    return new EntityNotFoundException(errorText);
                });

        return UserMapper.toDto(user);
    }

    @Override
    public void delete(long userId) {
        getById(userId);

        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getAll() {
        return UserMapper.toDto(userRepository.findAll());
    }

}