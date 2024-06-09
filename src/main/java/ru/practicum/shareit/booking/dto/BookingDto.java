package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Booking}
 */
@Data
@Builder
public class BookingDto implements Serializable {
    private Long id;

    private Long itemId;

    private String itemName;

    private String itemDescription;

    private Boolean itemAvailable;

    private Long bookerId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private BookingStatus status;

}