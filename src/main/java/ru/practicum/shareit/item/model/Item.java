package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

/**
 * Вещь
 */
@Data
@Builder
public class Item {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long ownerId;

    private Long requestId;

}