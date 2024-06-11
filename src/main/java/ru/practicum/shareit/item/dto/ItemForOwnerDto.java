package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * DTO for {@link Item} owner
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ItemForOwnerDto extends ItemDto {

    private BookingShortDto lastBooking;

    private BookingShortDto nextBooking;

}