package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Пользователь
 */
@Data
@Builder
@Jacksonized
@AllArgsConstructor
public class User {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    @NotBlank(message = "Адрес электронной почты не может быть пустым")
    @Email(message = "Формат адреса электронной почты неверный!")
    private String email;

}
