package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestForOwnerDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@Rollback(value = true)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceIntegrationTest {

    @Autowired
    private final RequestService requestService;

    @Autowired
    private final UserService userService;

    private UserDto userDto;
    private UserDto userDtoAnother;

    @BeforeEach
    void setUp() {
        userService.getAll().forEach(user -> userService.delete(user.getId()));
        userDto = userService.add(makeUserDto("user_1", "user_1_" +
                System.currentTimeMillis() + "@gmail.com"));

        userDtoAnother = userService.add(makeUserDto("user_another", "user_another_" +
                System.currentTimeMillis() + "@gmail.com"));
    }

    @Test
    void add_shouldAdd_whenRequestIsValid() {
        RequestDto requestDto = makeRequestDto("Request description");
        RequestDto returnedRequestDto = requestService.add(requestDto, userDto.getId());

        assertThat(returnedRequestDto.getId(), notNullValue());
        assertThat(returnedRequestDto.getDescription(), equalTo(requestDto.getDescription()));
    }

    @Test
    void getAllByOwnerId_shouldReturnRequests_whenExist() {
        RequestDto requestDto = makeRequestDto("Request description");
        requestService.add(requestDto, userDto.getId());

        List<RequestForOwnerDto> requests = requestService.getAllByOwnerId(userDto.getId());

        assertThat(requests, hasItem(hasProperty("description", equalTo(requestDto.getDescription()))));
    }

    @Test
    void getAllByUserIdAndPageable_shouldReturnRequests_whenExist() {
        RequestDto requestDto = makeRequestDto("Request description");
        requestService.add(requestDto, userDto.getId());

        List<RequestWithItemsDto> requests =
                requestService.getAllByUserIdAndPageable(userDtoAnother.getId(), 0, 10);

        assertThat(requests, hasItem(hasProperty("description", equalTo(requestDto.getDescription()))));
    }

    @Test
    void getRequestById_shouldReturnRequest_whenExists() {
        RequestDto requestDto = makeRequestDto("Request description");
        RequestDto savedRequest = requestService.add(requestDto, userDto.getId());

        RequestWithItemsDto foundRequest = requestService.getRequestById(userDto.getId(), savedRequest.getId());

        assertThat(foundRequest.getId(), equalTo(savedRequest.getId()));
        assertThat(foundRequest.getDescription(), equalTo(savedRequest.getDescription()));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void getRequestById_shouldError_whenNotExist() {
        long nonExistentRequestId = 999L;

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                requestService.getRequestById(userDto.getId(), nonExistentRequestId));

        assertThat(exception.getMessage(), containsString("Запрос на вещь не найден: " + nonExistentRequestId));
    }

    private RequestDto makeRequestDto(String description) {
        return RequestDto.builder()
                .description(description)
                .createdDate(LocalDateTime.now().toString())
                .build();
    }

    private UserDto makeUserDto(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }

}
