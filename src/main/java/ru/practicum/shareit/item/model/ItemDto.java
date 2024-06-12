package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link Item}
 */
@Data
public class ItemDto implements Serializable {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

}