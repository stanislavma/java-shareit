package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import jakarta.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.common.Constants.X_SHARER_USER_ID;

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
                                          @RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Добавление нового бронирования {}", bookingDto);
        return ResponseEntity.ok(bookingService.add(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateStatus(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                   @PathVariable Long bookingId,
                                                   @RequestParam boolean approved) {
        log.info("Обновление бронирования с ID {}", bookingId);
        return ResponseEntity.ok(bookingService.updateStatus(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getById(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                              @PathVariable Long bookingId) {
        log.info("Получить бронирование по ID - {}", bookingId);
        return ResponseEntity.ok(bookingService.getById(userId, bookingId));
    }

    @GetMapping()
    public ResponseEntity<List<BookingDto>> getAll(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        log.info("Получить все бронирования");
        return ResponseEntity.ok(bookingService.getAllByBookerId(userId, state, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getAllByOwnerId(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                            @RequestParam(defaultValue = "ALL") String state,
                                                            @RequestParam(defaultValue = "0") int from,
                                                            @RequestParam(defaultValue = "10") int size) {
        log.info("Получить все бронирования пользователя");
        return ResponseEntity.ok(bookingService.getAllByOwnerId(userId, state, from, size));
    }

}
