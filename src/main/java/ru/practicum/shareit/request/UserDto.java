package ru.practicum.shareit.request;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link ru.practicum.shareit.user.model.User}
 */
@Value
public class UserDto implements Serializable {
    Long id;
    String name;
    String email;
}