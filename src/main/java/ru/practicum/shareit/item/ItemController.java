package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
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
    public ResponseEntity<ItemDto> getById(@PathVariable Long itemId) {
        log.info("Получить вещь по ID - {}", itemId);
        return ResponseEntity.ok(itemService.getById(itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получить вещи по id владельцу");
        return ResponseEntity.ok(itemService.getItemsByOwnerId(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> getItemsByText(@RequestParam String text) {
        log.info("Получить вещи по тексту в названии и описании");
        return ResponseEntity.ok(itemService.getItemsByText(text));
    }

}