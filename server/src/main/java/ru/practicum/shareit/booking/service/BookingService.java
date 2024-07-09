package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import java.util.List;

public interface BookingService {

    BookingDto add(BookingDto bookingDto, Long userId);

    BookingDto updateStatus(Long bookingId, Long userId, boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getAllByBookerId(long userId, String state, Integer from, Integer size);

    List<BookingDto> getAllByOwnerId(long userId, String state, Integer from, Integer size);

}
