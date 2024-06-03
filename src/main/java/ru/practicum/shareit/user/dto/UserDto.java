package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;

@Data
@Builder
@Jacksonized
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDto {

    private Long id;

    private String name;

    @Email(message = "Формат адреса электронной почты неверный!")
    private String email;

}
