package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import java.io.Serializable;

/**
 * DTO for User
 */
@Data
@Builder
@Jacksonized
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDto implements Serializable {

    private Long id;

    @NotEmpty(message = "Не должно быть пустым")
    private String name;

    @NotEmpty(message = "Адрес электронной почты не может быть пустым")
    @Email(message = "Формат адреса электронной почты неверный!")
    private String email;

}
