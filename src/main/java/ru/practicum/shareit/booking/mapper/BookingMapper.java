package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static BookingDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        String startDate = formatter.format(booking.getStartDate());
        String endDate = formatter.format(booking.getEndDate());

        ItemDto itemDto = ItemMapper.toItemDto(booking.getItem());
        UserDto bookerDto = UserMapper.toUserDto(booking.getBooker());

        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem() != null ? booking.getItem().getId() : null)
                .itemDto(itemDto)
                .bookerId(booking.getBooker() != null ? booking.getBooker().getId() : null)
                .bookerDto(bookerDto)
                .startDate(startDate)
                .endDate(endDate)
                .status(booking.getStatus())
                .build();
    }

    public static BookingShortDto toShortDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return BookingShortDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker() != null ? booking.getBooker().getId() : null)
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .build();
    }

    public static List<BookingDto> toDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    public static Booking toEntity(BookingDto dto, Item item, User booker) {
        if (dto == null) {
            return null;
        }

        LocalDateTime startDate = LocalDateTime.parse(dto.getStartDate(), formatter);
        LocalDateTime endDate = LocalDateTime.parse(dto.getEndDate(), formatter);

        return Booking.builder()
                .id(dto.getId())
                .item(item)
                .booker(booker)
                .startDate(startDate)
                .endDate(endDate)
                .status(dto.getStatus() != null ? dto.getStatus() : BookingStatus.WAITING)
                .build();
    }

}
