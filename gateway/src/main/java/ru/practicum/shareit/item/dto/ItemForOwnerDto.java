package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

/**
 * DTO for Item owner
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ItemForOwnerDto extends ItemDto  {

    private BookingShortDto lastBooking;

    private BookingShortDto nextBooking;

    private List<CommentDto> comments;

}