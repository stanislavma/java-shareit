package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * Вещь
 */
@Data
@Builder
public class ItemDto {

    private Long id;

    @NotNull(message = "Имя вещи является обязательным!")
    private String name;

    @NotNull(message = "Описание вещи не может быть пустым!")
    private String description;

    @NotNull(message = "Доступность вещи является обязательным!")
    private Boolean available;

    private Long ownerId;

    private Long requestId;

}