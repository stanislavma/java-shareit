package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto add(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long userId);

    ItemDto getById(Long userId, Long itemId);

    List<ItemDto> getItemsByOwnerId(Long userId);

    List<ItemDto> getItemsByText(String text);

}