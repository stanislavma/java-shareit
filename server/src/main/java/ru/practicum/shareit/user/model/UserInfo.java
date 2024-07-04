package ru.practicum.shareit.user.model;

/**
 * Projection for {@link User}
 */
public interface UserInfo {
    Long getId();

    String getName();

    String getEmail();
}