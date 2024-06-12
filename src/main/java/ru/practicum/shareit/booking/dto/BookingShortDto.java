package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO short version for {@link Booking}
 */
@Data
@Builder
public class BookingShortDto implements Serializable {

    private Long id;

    private Long bookerId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

}