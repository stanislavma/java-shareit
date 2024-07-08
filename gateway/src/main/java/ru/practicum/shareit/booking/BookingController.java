package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingDto;

import static ru.practicum.shareit.common.Constants.X_SHARER_USER_ID;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody BookingDto bookingDto,
                                      @RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Добавление нового бронирования {}", bookingDto);
        return bookingClient.add(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                               @PathVariable Long bookingId,
                                               @RequestParam boolean approved) {
        log.info("Обновление бронирования с ID {}", bookingId);
        return bookingClient.updateStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                          @PathVariable Long bookingId) {
        log.info("Получить бронирование по ID - {}", bookingId);
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAll(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                         @RequestParam(defaultValue = "ALL") String state,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                         @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получить все бронирования");
        return bookingClient.getAllByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwnerId(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                  @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получить все бронирования пользователя");
        return bookingClient.getAllByOwnerId(userId, state, from, size);
    }

}
