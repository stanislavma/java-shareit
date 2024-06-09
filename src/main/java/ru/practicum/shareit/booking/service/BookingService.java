package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto add(BookingDto bookingDto, Long userId);

    BookingDto update(BookingDto bookingDto, Long userId);

    BookingDto updateStatus(Long bookingId, Long userId, boolean approved);

    BookingDto getById(Long bookingId);

    List<BookingDto> getAll(long userId);
    List<BookingDto> getAllByUserId(long userId);

}
