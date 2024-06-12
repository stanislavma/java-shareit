package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * DTO for {@link User}
 */
@Data
@Builder
@Jacksonized
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDto {

    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty(message = "Адрес электронной почты не может быть пустым")
    @Email(message = "Формат адреса электронной почты неверный!")
    private String email;

}
