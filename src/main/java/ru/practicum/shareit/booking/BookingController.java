package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * Booking rest controller
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> add(@Valid @RequestBody BookingDto bookingDto,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Добавление нового бронирования {}", bookingDto);
        return ResponseEntity.ok(bookingService.add(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PathVariable Long bookingId,
                                                   @RequestParam boolean approved) {
        log.info("Обновление бронирования с ID {}", bookingId);
        return ResponseEntity.ok(bookingService.updateStatus(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long bookingId) {
        log.info("Получить бронирование по ID - {}", bookingId);
        return ResponseEntity.ok(bookingService.getById(userId, bookingId));
    }

    @GetMapping()
    public ResponseEntity<List<BookingDto>> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Получить все бронирования");

        return ResponseEntity.ok(bookingService.getAllByBookerId(userId, state));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получить все бронирования пользователя");

        BookingState bookingState = getBookingState(state);

        return ResponseEntity.ok(bookingService.getAllByOwnerId(userId, bookingState));
    }

    private static BookingState getBookingState(String state) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }

        return bookingState;
    }

}
