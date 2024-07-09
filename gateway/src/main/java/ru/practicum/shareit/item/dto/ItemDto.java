package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.common.ValidationGroups;

import java.io.Serializable;

/**
 * DTO for Item
 */
@Data
@SuperBuilder
@Jacksonized
public class ItemDto implements Serializable {

    private Long id;

    @NotNull(groups = ValidationGroups.Create.class)
    @NotBlank(message = "Имя вещи является обязательным!")
    @Size(max = 300, message = "Максимальная длина - 300 символов")
    private String name;

    @NotEmpty(message = "Описание вещи не может быть пустым!")
    @Size(max = 300, message = "Максимальная длина - 300 символов")
    private String description;

    @NotNull(message = "Доступность вещи является обязательным!",
            groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Boolean available;

    private Long ownerId;

    private Long requestId;

}