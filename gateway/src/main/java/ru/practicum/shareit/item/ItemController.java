package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.ValidationGroups;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static ru.practicum.shareit.common.Constants.X_SHARER_USER_ID;

@Slf4j
@Controller
@AllArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Validated({ValidationGroups.Create.class, Default.class}) ItemDto itemDto,
                                      @RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Добавление новой вещи {}", itemDto);
        return itemClient.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable Long itemId, @RequestBody ItemDto itemDto,
                                         @RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Обновление вещи с ID {}", itemId);
        itemDto.setId(itemId);
        return itemClient.update(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                          @PathVariable Long itemId) {
        log.info("Получить вещь по ID - {}", itemId);
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwnerId(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size) {
        log.info("Получить все вещи владельца - {}", userId);
        return itemClient.getItemsByOwnerId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByText(@RequestParam String text,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "10") int size) {
        log.info("Получить вещи по тексту в названии и описании");
        return itemClient.getItemsByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("Добавить комментарий к вещи с ID - {}", itemId);
        return itemClient.addComment(commentDto, userId, itemId);
    }

}