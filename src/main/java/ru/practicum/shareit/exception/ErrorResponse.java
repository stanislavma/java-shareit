package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String description;

    public ErrorResponse(String description) {
        this.description = description;
    }
}
