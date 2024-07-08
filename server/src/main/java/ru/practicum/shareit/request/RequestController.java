package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestForOwnerDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.service.RequestService;

import jakarta.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.common.Constants.X_SHARER_USER_ID;

/**
 * Requests for item rest controller
 */
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestDto> add(@Valid @RequestBody RequestDto requestDto,
                                          @RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Добавление нового запроса на вещь {}", requestDto);
        return ResponseEntity.ok(requestService.add(requestDto, userId));
    }

    @GetMapping
    public ResponseEntity<List<RequestForOwnerDto>> getAllByOwnerId(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Получить все запросы владельца - {}", userId);
        return ResponseEntity.ok(requestService.getAllByOwnerId(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestWithItemsDto>> getAllByUserIdAndPageable(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                                               @RequestParam(defaultValue = "0") int from,
                                                                               @RequestParam(defaultValue = "10") int size) {
        log.info("Получение всех запросов, кроме тех, у которых requestorId равен {}", userId);
        return ResponseEntity.ok(requestService.getAllByUserIdAndPageable(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestWithItemsDto> getById(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                       @PathVariable Long requestId) {
        log.info("Получить вещь по ID - {}", requestId);
        return ResponseEntity.ok(requestService.getRequestById(userId, requestId));
    }

}
