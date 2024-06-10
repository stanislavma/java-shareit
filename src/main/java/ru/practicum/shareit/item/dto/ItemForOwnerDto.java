package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * DTO for {@link Item}
 */
@Data
@Builder
@Jacksonized
public class ItemForOwnerDto {

    private Long id;

    @NotEmpty(message = "Имя вещи является обязательным!")
    private String name;

    @NotEmpty(message = "Описание вещи не может быть пустым!")
    private String description;

    @NotNull(message = "Доступность вещи является обязательным!")
    private Boolean available;

    private Long ownerId;

    private Long requestId;

}