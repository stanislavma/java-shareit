package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@Rollback(value = false)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIntegrationTest {

    private final UserService userService;
    private final UserRepository userRepository;

    @Test
    void add_shouldAdd_whenUserIsValid() {
        //given
        UserDto userDto = makeUserDto("user_1", "user_1@gmail.com");

        //that
        UserDto returnedUserDto = userService.add(userDto);

        //then
        assertThat(returnedUserDto.getId(), notNullValue());
        assertThat(returnedUserDto.getName(), equalTo(userDto.getName()));
        assertThat(returnedUserDto.getEmail(), equalTo(userDto.getEmail()));

        User savedUser = userRepository.findById(returnedUserDto.getId()).orElseThrow();
        assertThat(savedUser.getName(), equalTo(userDto.getName()));
        assertThat(savedUser.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void update_shouldUpdate_whenUserIsValid() {
        //given
        UserDto userDto = makeUserDto("user_4", "user_4@gmail.com");
        UserDto savedUser = userService.add(userDto);
        UserDto updateUserDto = UserDto.builder()
                .id(savedUser.getId())
                .name("updated_user_4")
                .email("updated_user_4@gmail.com")
                .build();

        //that
        UserDto updatedUser = userService.update(updateUserDto);

        //then
        assertThat(updatedUser.getId(), equalTo(savedUser.getId()));
        assertThat(updatedUser.getName(), equalTo(updateUserDto.getName()));
        assertThat(updatedUser.getEmail(), equalTo(updateUserDto.getEmail()));

        User dbUser = userRepository.findById(updatedUser.getId()).orElseThrow();
        assertThat(dbUser.getName(), equalTo(updateUserDto.getName()));
        assertThat(dbUser.getEmail(), equalTo(updateUserDto.getEmail()));
    }

    @Test
    void delete_shouldDelete_whenUserExists() {
        //given
        UserDto userDto = makeUserDto("user_5", "user_5@gmail.com");
        UserDto savedUser = userService.add(userDto);

        //that
        userService.delete(savedUser.getId());

        //then
        assertThrows(EntityNotFoundException.class, () -> userService.getById(savedUser.getId()));

        boolean exists = userRepository.existsById(savedUser.getId());
        assertThat(exists, is(false));
    }

    @Test
    void getAll_shouldReturnUsers_whenUsersIsValid() {
        //given
        UserDto userDto1 = makeUserDto("user_6", "user_6@gmail.com");
        UserDto userDto2 = makeUserDto("user_7", "user_7@gmail.com");

        UserDto savedUser1 = userService.add(userDto1);
        UserDto savedUser2 = userService.add(userDto2);

        //that
        List<UserDto> users = userService.getAll();

        //then
        assertThat(users, hasItem(allOf(
                hasProperty("name", equalTo(userDto1.getName())),
                hasProperty("email", equalTo(userDto1.getEmail()))
        )));
        assertThat(users, hasItem(allOf(
                hasProperty("name", equalTo(userDto2.getName())),
                hasProperty("email", equalTo(userDto2.getEmail()))
        )));

        User dbUser1 = userRepository.findById(savedUser1.getId()).orElseThrow();
        User dbUser2 = userRepository.findById(savedUser2.getId()).orElseThrow();

        assertThat(dbUser1.getName(), equalTo(userDto1.getName()));
        assertThat(dbUser1.getEmail(), equalTo(userDto1.getEmail()));
        assertThat(dbUser2.getName(), equalTo(userDto2.getName()));
        assertThat(dbUser2.getEmail(), equalTo(userDto2.getEmail()));
    }

    @Test
    void getById_shouldReturnUser_whenUserExists() {
        //given
        UserDto userDto = makeUserDto("user_3", "user_3@gmail.com");
        UserDto savedUser = userService.add(userDto);

        //that
        UserDto foundUser = userService.getById(savedUser.getId());

        //then
        assertThat(foundUser.getId(), equalTo(savedUser.getId()));
        assertThat(foundUser.getName(), equalTo(savedUser.getName()));
        assertThat(foundUser.getEmail(), equalTo(savedUser.getEmail()));

        User dbUser = userRepository.findById(foundUser.getId()).orElseThrow();
        assertThat(dbUser.getName(), equalTo(savedUser.getName()));
        assertThat(dbUser.getEmail(), equalTo(savedUser.getEmail()));
    }

    @Test
    void getById_shouldError_whenUserNotExist() {
        //given
        long nonExistentUserId = 999L;

        //that
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.getById(nonExistentUserId);
        });

        //then
        assertThat(exception.getMessage(), containsString("Пользователь не найден"));
    }

    private UserDto makeUserDto(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }

}