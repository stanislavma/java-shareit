package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;

import java.util.List;

public interface ItemService {

    ItemDto add(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long userId);

    ItemForOwnerDto getById(Long userId, Long itemId);

    List<ItemForOwnerDto> getItemsByOwnerId(Long userId);

    List<ItemDto> getItemsByText(String text);

    CommentDto addComment(CommentDto commentDto, Long userId, Long itemId);

}