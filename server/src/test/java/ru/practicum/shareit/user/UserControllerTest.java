package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userServiceMock;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("Ivan")
                .email("ivanov@gmail.com")
                .build();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoUsers() throws Exception {
        when(userServiceMock.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAll_shouldReturnUsers_whenUserExists() throws Exception {
        when(userServiceMock.getAll()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(userDto.getId()))
                .andExpect(jsonPath("$[0].name").value(userDto.getName()))
                .andExpect(jsonPath("$[0].email").value(userDto.getEmail()));
    }

    @Test
    void getById_shouldReturnUser_whenUserExists() throws Exception {
        when(userServiceMock.getById(anyLong())).thenReturn(userDto);

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void getById_shouldReturnError_whenUserNotExist() throws Exception {
        long nonExistentUserId = 999L;
        String expectedErrorMessage = "Пользователь не найден: " + nonExistentUserId;

        when(userServiceMock.getById(nonExistentUserId)).thenThrow(new EntityNotFoundException(expectedErrorMessage));

        mockMvc.perform(get("/users/" + nonExistentUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));
    }

    @Test
    void add_shouldReturnCreatedUser_whenUserIsValid() throws Exception {
        when(userServiceMock.add(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void add_shouldReturnBadRequest_whenNameIsEmpty() throws Exception {
        UserDto invalidUserDto = UserDto.builder()
                .name("")
                .email("ivanov@gmail.com")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("{name=Не должно быть пустым}"));
    }

    @Test
    void add_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        UserDto invalidUserDto = UserDto.builder()
                .name("Ivan")
                .email("ivanov-gmail.com")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("{email=Формат адреса электронной почты неверный!}"));
    }

    @Test
    void add_shouldReturnBadRequest_whenEmailIsEmpty() throws Exception {
        UserDto invalidUserDto = UserDto.builder()
                .name("Ivan")
                .email("")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("{email=Адрес электронной почты не может быть пустым}"));
    }

    @Test
    void update_shouldReturnUpdatedUser_whenUserIsValid() throws Exception {
        when(userServiceMock.update(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(patch("/users/{userId}", userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void delete_shouldReturnNoContent_whenUserDeleted() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

}