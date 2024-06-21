package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("Valid User")
                .email("valid_user@gmail.com")
                .build();

        user = UserMapper.toEntity(userDto);
    }

    @Test
    void add_shouldAddUser_whenUserIsValid() {
        when(userRepository.save(user)).thenReturn(user);

        UserDto returnedUserDto = userService.add(userDto);

        assertNotNull(returnedUserDto);

        verify(userRepository).save(user);

        assertEquals(userDto.getId(), returnedUserDto.getId());
        assertEquals(userDto.getName(), returnedUserDto.getName());
        assertEquals(userDto.getEmail(), returnedUserDto.getEmail());
    }

    @Test
    void update_shouldUpdateUser_whenUserIsValid() {
        // given
        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .name("Petrov")
                .email("petr@gmail.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(UserMapper.toEntity(updatedUserDto));
        when(userRepository.findByEmail("petr@gmail.com")).thenReturn(Optional.empty());

        UserDto result = userService.update(updatedUserDto);

        assertNotNull(result);
        assertEquals(updatedUserDto.getId(), result.getId());
        assertEquals(updatedUserDto.getName(), result.getName());
        assertEquals(updatedUserDto.getEmail(), result.getEmail());

        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("petr@gmail.com");
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    void update_whenEmailExists_thenThrowValidationException() {
        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .name("Petr")
                .email("petr@gmail.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("petr@gmail.com")).thenReturn(Optional.of(new User()));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.update(updatedUserDto);
        });

        assertEquals("Email уже имеется в системе", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("petr@gmail.com");
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void update_whenUserNotFound_thenThrowEntityNotFoundException() {
        // given
        UserDto updatedUserDto = UserDto.builder()
                .id(2L)
                .name("Ivan")
                .email("ivanov@gmail.com")
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.update(updatedUserDto);
        });

        // then
        assertEquals("Пользователь не найден: 2", exception.getMessage());
        verify(userRepository).findById(2L);
        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    void getById_shouldReturnUser_whenUserExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        long findingUserId = 1L;
        UserDto returnedUserDto = userService.getById(findingUserId);

        assertEquals(userDto.getId(), returnedUserDto.getId());
        assertEquals(userDto.getName(), returnedUserDto.getName());
        assertEquals(userDto.getEmail(), returnedUserDto.getEmail());

        verify(userRepository, times(1)).findById(findingUserId);
    }

    @Test
    void getById_shouldReturnError_whenUserNotExists() {
        long notExistUserId = 999L;
        when(userRepository.findById(notExistUserId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getById(notExistUserId));

        verify(userRepository, times(1)).findById(notExistUserId);
    }


    @Test
    void update() {
    }

    @Test
    void delete() {
    }


    @Test
    void getAll() {
    }
}