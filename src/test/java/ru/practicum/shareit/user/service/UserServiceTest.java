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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    void update_shouldUpdateUser_whenOnlyNameIsUpdated() {
        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .name("Petrov")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(userRepository.saveAndFlush(any(User.class)))
                .thenReturn(UserMapper.toEntity(updatedUserDto));

        UserDto result = userService.update(updatedUserDto);

        assertNotNull(result);
        assertEquals(updatedUserDto.getId(), result.getId());
        assertEquals(updatedUserDto.getName(), result.getName());
        assertNull(result.getEmail());

        verify(userRepository).findById(1L);
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    void update_shouldUpdateUser_whenOnlyEmailIsUpdated() {
        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .email("petr@gmail.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(UserMapper.toEntity(updatedUserDto));
        when(userRepository.findByEmail("petr@gmail.com")).thenReturn(Optional.empty());

        UserDto result = userService.update(updatedUserDto);

        assertNotNull(result);
        assertEquals(updatedUserDto.getId(), result.getId());
        assertEquals(updatedUserDto.getEmail(), result.getEmail());
        assertNull(result.getName());

        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("petr@gmail.com");
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    void update_shouldUpdateUser_whenEmailIsDifferent() {
        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .name("Valid User")
                .email("not_equals_email@gmail.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(UserMapper.toEntity(updatedUserDto));
        when(userRepository.findByEmail("not_equals_email@gmail.com")).thenReturn(Optional.empty());

        UserDto result = userService.update(updatedUserDto);

        assertNotNull(result);
        assertEquals(updatedUserDto.getId(), result.getId());
        assertEquals(updatedUserDto.getName(), result.getName());
        assertEquals(updatedUserDto.getEmail(), result.getEmail());

        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("not_equals_email@gmail.com");
        verify(userRepository).saveAndFlush(any(User.class));
    }
    
    @Test
    void update_shouldNotUpdateUser_whenNoFieldIsUpdated() {
        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        UserDto result = userService.update(updatedUserDto);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());

        verify(userRepository).findById(1L);
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
        UserDto updatedUserDto = UserDto.builder()
                .id(2L)
                .name("Ivan")
                .email("ivanov@gmail.com")
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.update(updatedUserDto);
        });

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
    void delete_shouldDeleteUser_whenUserExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(anyLong());

        long deletingUserId = 1L;
        userService.delete(deletingUserId);

        verify(userRepository, times(1)).findById(deletingUserId);
        verify(userRepository, times(1)).deleteById(deletingUserId);
    }

    @Test
    void delete_shouldThrowException_whenUserNotExists() {
        long notExistUserId = 999L;
        when(userRepository.findById(notExistUserId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.delete(notExistUserId));

        verify(userRepository, times(1)).findById(notExistUserId);
        verify(userRepository, never()).deleteById(notExistUserId);
    }

    @Test
    void getAll_shouldReturnUsers() {
        UserDto anotherUserDto = UserDto.builder()
                .id(2L)
                .name("Another User")
                .email("another_user@gmail.com")
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user, UserMapper.toEntity(anotherUserDto)));

        List<UserDto> users = userService.getAll();

        assertNotNull(users);
        assertEquals(2, users.size());

        assertEquals(userDto.getId(), users.get(0).getId());
        assertEquals(userDto.getName(), users.get(0).getName());
        assertEquals(userDto.getEmail(), users.get(0).getEmail());

        assertEquals(anotherUserDto.getId(), users.get(1).getId());
        assertEquals(anotherUserDto.getName(), users.get(1).getName());
        assertEquals(anotherUserDto.getEmail(), users.get(1).getEmail());

        verify(userRepository, times(1)).findAll();
    }

}