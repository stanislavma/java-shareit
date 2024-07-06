package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
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
