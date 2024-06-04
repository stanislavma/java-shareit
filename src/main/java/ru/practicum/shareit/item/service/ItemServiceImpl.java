package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        validateIsUserExists(userId); // если пользователя нет, то метод сам пробросит ошибку

        itemDto.setOwnerId(userId);
        return ItemMapper.toItemDto(itemStorage.add(ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long userId) {
        validateIsUserExists(userId); // если пользователя нет, то метод сам пробросит ошибку

        ItemDto existingItem = getById(itemDto.getId());

        if (!existingItem.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("Нет доступа к вещи!");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemStorage.update(ItemMapper.toItem(existingItem)));
    }

    @Override
    public ItemDto getById(Long itemId) {
        Item item = itemStorage.getById(itemId)
                .orElseThrow(() -> {
                    String errorText = "Вещь не найдена: " + itemId;
                    log.error(errorText);
                    return new EntityNotFoundException(errorText);
                });

        return ItemMapper.toItemDto(item);
    }

    public List<ItemDto> getItemsByOwnerId(Long userId) {
        return ItemMapper.toItemDto(itemStorage.getItemsByOwnerId(userId));
    }

    public List<ItemDto> getItemsByText(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }

        return ItemMapper.toItemDto(itemStorage.getItemsByText(text));
    }

    private void validateIsUserExists(long userId) {
        userStorage.getById(userId)
                .orElseThrow(() -> {
                    String errorText = "Пользователь не найден: " + userId;
                    log.error(errorText);
                    return new EntityNotFoundException(errorText);
                });
    }

}