package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ItemStorageMemoryImpl implements ItemStorage {

    private final HashMap<Long, Item> items = new HashMap<>();
    private long itemIdCounter = 1;

    @Override
    public Item add(Item item) {
        if (item.getId() == null) {
            item.setId(itemIdCounter++);
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        if (item.getId() == null) {
            item.setId(itemIdCounter++);
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> getById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> getItemsByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemsByText(String text) {
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }

}