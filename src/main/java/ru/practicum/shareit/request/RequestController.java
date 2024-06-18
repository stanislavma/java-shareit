package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;

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
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Добавление нового запроса на вещь {}", requestDto);
        return ResponseEntity.ok(requestService.add(requestDto, userId));
    }

}
