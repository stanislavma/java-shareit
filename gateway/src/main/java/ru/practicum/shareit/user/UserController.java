package ru.practicum.shareit.user;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.common.ValidationGroups;

/**
 * User rest controller
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> add(
            @RequestBody @Validated({ValidationGroups.Create.class, Default.class}) UserDto userDto) {
        log.info("Добавление нового пользователя {}", userDto);
        return userClient.add(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable Long userId,
                                         @RequestBody @Validated({ValidationGroups.Update.class, Default.class}) UserDto userDto) {
        log.info("Обновление пользователя с ID {}", userId);
        userDto.setId(userId);
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable long id) {
        log.info("Удаление пользователя {}", id);
        userClient.delete(id);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable long id) {
        log.info("Получить пользователя по ID - {}", id);
        return userClient.getById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Получить пользователей");
        return userClient.getAll();
    }

}
