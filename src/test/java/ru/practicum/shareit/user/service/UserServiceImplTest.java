package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@Rollback(value = false)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private final UserService service;

    @Test
    void add_shouldAdd_whenUserIsValid() {
        UserDto userDto = makeUserDto("user_1", "user_1@gmail.com");
        UserDto returnedUserDto = service.add(userDto);

        assertThat(returnedUserDto.getId(), notNullValue());
        assertThat(returnedUserDto.getName(), equalTo(userDto.getName()));
        assertThat(returnedUserDto.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void update_shouldUpdate_whenUserIsValid() {
        UserDto userDto = makeUserDto("user_4", "user_4@gmail.com");
        UserDto savedUser = service.add(userDto);

        UserDto updateUserDto = UserDto.builder()
                .id(savedUser.getId())
                .name("updated_user_4")
                .email("updated_user_4@gmail.com")
                .build();

        UserDto updatedUser = service.update(updateUserDto);

        assertThat(updatedUser.getId(), equalTo(savedUser.getId()));
        assertThat(updatedUser.getName(), equalTo(updateUserDto.getName()));
        assertThat(updatedUser.getEmail(), equalTo(updateUserDto.getEmail()));
    }

    @Test
    void delete_shouldDelete_whenUserExists() {
        UserDto userDto = makeUserDto("user_5", "user_5@gmail.com");
        UserDto savedUser = service.add(userDto);

        service.delete(savedUser.getId());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            service.getById(savedUser.getId());
        });

        assertThat(exception.getMessage(), containsString("Пользователь не найден"));
    }

    @Test
    void getAll_shouldReturnUsers_whenUsersIsValid() {
        UserDto userDto1 = makeUserDto("user_6", "user_6@gmail.com");
        UserDto userDto2 = makeUserDto("user_7", "user_7@gmail.com");

        service.add(userDto1);
        service.add(userDto2);

        List<UserDto> users = service.getAll();

        assertThat(users, hasItem(allOf(
                hasProperty("name", equalTo(userDto1.getName())),
                hasProperty("email", equalTo(userDto1.getEmail()))
        )));
        assertThat(users, hasItem(allOf(
                hasProperty("name", equalTo(userDto2.getName())),
                hasProperty("email", equalTo(userDto2.getEmail()))
        )));
    }

    @Test
    void getById_shouldReturnUser_whenUserExists() {
        UserDto userDto = makeUserDto("user_3", "user_3@gmail.com");
        UserDto savedUser = service.add(userDto);

        UserDto foundUser = service.getById(savedUser.getId());

        assertThat(foundUser.getId(), equalTo(savedUser.getId()));
        assertThat(foundUser.getName(), equalTo(savedUser.getName()));
        assertThat(foundUser.getEmail(), equalTo(savedUser.getEmail()));
    }

    @Test
    void getById_shouldThrowException_whenUserDoesNotExist() {
        long nonExistentUserId = 999L;

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            service.getById(nonExistentUserId);
        });

        assertThat(exception.getMessage(), containsString("Пользователь не найден"));
    }

    private UserDto makeUserDto(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }

}