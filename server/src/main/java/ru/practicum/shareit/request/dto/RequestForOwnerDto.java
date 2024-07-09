package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

/**
 * DTO for {@link Request} owner
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class RequestForOwnerDto extends RequestDto   {

    private List<ItemDto> items;

}