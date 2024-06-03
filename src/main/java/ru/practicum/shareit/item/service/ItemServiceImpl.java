package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new ValidationException("Имя вещи является обязательным!", HttpStatus.BAD_REQUEST);
        }
        itemDto.setOwnerId(userId);
        return itemStorage.add(itemDto);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long userId) {
        ItemDto existingItem = itemStorage.getById(itemDto.getId())
                .orElseThrow(() -> {
                    String errorText = "Вещь не найдена: " + itemDto.getId();
                    log.error(errorText);
                    return new EntityNotFoundException(errorText);
                });

        if (!existingItem.getOwnerId().equals(userId)) {
            throw new EntityNotFoundException("Item not found!");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }

        existingItem.setAvailable(itemDto.isAvailable());

        return itemStorage.update(existingItem);
    }

    @Override
    public ItemDto getById(Long itemId) {
        return itemStorage.getById(itemId)
                .orElseThrow(() -> {
                    String errorText = "Вещь не найдена: " + itemId;
                    log.error(errorText);
                    return new EntityNotFoundException(errorText);
                });
    }

    public List<ItemDto> getItemsByOwnerId(Long userId) {
        return itemStorage.getItemsByOwnerId(userId);
    }

    public List<ItemDto> getItemsByText(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemStorage.getItemsByText(text);
    }

}