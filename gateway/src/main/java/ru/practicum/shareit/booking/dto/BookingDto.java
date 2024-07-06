package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.Serializable;

/**
 * DTO for Booking
 */
@Data
@Builder
@Jacksonized
public class BookingDto implements Serializable {

    private Long id;

    @NotNull(message = "Бронируемая вещь является обязательным полем")
    private Long itemId;

    @JsonProperty("item")
    private ItemDto itemDto;

    private Long bookerId;

    @JsonProperty("booker")
    private UserDto bookerDto;

    @JsonProperty("start")
    @NotEmpty(message = "Дата начала бронирования является обязательным полем")
    private String startDate;

    @JsonProperty("end")
    @NotEmpty(message = "Дата завершения бронирования является обязательным полем")
    private String endDate;

    @Builder.Default
    private BookingStatus status = BookingStatus.WAITING;

}