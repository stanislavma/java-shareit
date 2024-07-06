package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.service.ItemService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> add(@Valid @RequestBody ItemDto itemDto,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Добавление новой вещи {}", itemDto);
        return ResponseEntity.ok(itemService.add(itemDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@PathVariable Long itemId, @RequestBody ItemDto itemDto,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Обновление вещи с ID {}", itemId);
        itemDto.setId(itemId);
        return ResponseEntity.ok(itemService.update(itemDto, userId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemForOwnerDto> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PathVariable Long itemId) {
        log.info("Получить вещь по ID - {}", itemId);
        return ResponseEntity.ok(itemService.getById(userId, itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemForOwnerDto>> getItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                   @RequestParam(defaultValue = "0") int from,
                                                                   @RequestParam(defaultValue = "10") int size) {
        log.info("Получить все вещи владельца - {}", userId);
        return ResponseEntity.ok(itemService.getItemsByOwnerId(userId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> getItemsByText(@RequestParam String text,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "10") int size) {
        log.info("Получить вещи по тексту в названии и описании");
        return ResponseEntity.ok(itemService.getItemsByText(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long itemId,
                                                 @Valid @RequestBody CommentDto commentDto) {
        log.info("Добавить комментарий к вещи с ID - {}", itemId);
        return ResponseEntity.ok(itemService.addComment(commentDto, userId, itemId));
    }

}