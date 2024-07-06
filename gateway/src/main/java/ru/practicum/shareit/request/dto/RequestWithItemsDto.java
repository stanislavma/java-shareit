package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * DTO for Request owner with items
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class RequestWithItemsDto extends RequestDto  {

    private List<ItemDto> items;

}