package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.map.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        findUserById(userId);

        itemDto.setOwnerId(userId);
        User user = findUserById(userId);
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto, user, null)));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long userId) {
        findUserById(userId); // если пользователя нет, то метод сам пробросит ошибку

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

        User user = findUserById(userId);
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(existingItem, user, null)));
    }

    @Override
    public ItemDto getById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    String errorText = "Вещь не найдена: " + itemId;
                    log.error(errorText);
                    return new EntityNotFoundException(errorText);
                });

        return ItemMapper.toItemDto(item);
    }

    public List<ItemDto> getItemsByOwnerId(Long userId) {
        return ItemMapper.toItemDto(itemRepository.findAllByOwnerId(userId));
    }

    public List<ItemDto> getItemsByText(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }

        return ItemMapper.toItemDto(itemRepository.findByText(text));
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    String errorText = "Пользователь не найден: " + userId;
                    log.error(errorText);
                    return new EntityNotFoundException(errorText);
                });
    }

}