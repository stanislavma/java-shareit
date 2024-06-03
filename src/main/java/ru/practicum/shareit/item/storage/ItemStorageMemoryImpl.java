package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ItemStorageMemoryImpl implements ItemStorage {

    private final HashMap<Long, ItemDto> items = new HashMap<>();
    private long itemIdCounter = 1;

    @Override
    public ItemDto add(ItemDto item) {
        if (item.getId() == null) {
            item.setId(itemIdCounter++);
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public ItemDto update(ItemDto item) {
        if (item.getId() == null) {
            item.setId(itemIdCounter++);
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<ItemDto> getById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(ItemDto::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }

}