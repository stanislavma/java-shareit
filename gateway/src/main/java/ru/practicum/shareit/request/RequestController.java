package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

/**
 * Requests for item rest controller
 */
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody RequestDto requestDto,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Добавление нового запроса на вещь {}", requestDto);
        return requestClient.add(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получить все запросы владельца - {}", userId);
        return requestClient.getAllByOwnerId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllByUserIdAndPageable(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                               @RequestParam(defaultValue = "0") int from,
                                                                               @RequestParam(defaultValue = "10") int size) {
        log.info("Получение всех запросов, кроме тех, у которых requestorId равен {}", userId);
        return requestClient.getAllByUserIdAndPageable(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @PathVariable Long requestId) {
        log.info("Получить вещь по ID - {}", requestId);
        return requestClient.getRequestById(userId, requestId);
    }

}
