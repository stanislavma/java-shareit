package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * Пользователь
 */
@Data
@Builder
@Jacksonized
@AllArgsConstructor
public class User {

    private Long id;

    private String name;

    private String email;

}
