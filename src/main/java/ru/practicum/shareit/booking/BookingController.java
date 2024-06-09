package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.dto.UserDto;

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
    public ResponseEntity<BookingDto> updateStatus(@PathVariable Long bookingId,
                                             @RequestParam boolean approved,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Обновление бронирования с ID {}", bookingId);
        return ResponseEntity.ok(bookingService.updateStatus(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getById(@PathVariable Long bookingId) {
        log.info("Получить бронирование по ID - {}", bookingId);
        return ResponseEntity.ok(bookingService.getById(bookingId));
    }

    @GetMapping()
    public ResponseEntity<List<BookingDto>> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получить все бронирования");
        return ResponseEntity.ok(bookingService.getAll(userId));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получить все бронирования пользователя");
        return ResponseEntity.ok(bookingService.getAllByUserId(userId));
    }


}
