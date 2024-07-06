package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO short version for Booking
 */
@Data
@Builder
public class BookingShortDto implements Serializable {

    private Long id;

    private Long bookerId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

}