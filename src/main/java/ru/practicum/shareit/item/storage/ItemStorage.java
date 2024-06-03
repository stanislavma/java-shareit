package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    ItemDto add(ItemDto item);
    ItemDto update(ItemDto item);

    Optional<ItemDto> getById(Long id);

    List<ItemDto> getItemsByOwnerId(Long ownerId);

    List<ItemDto> getItemsByText(String text);

    void delete(Long id);

}