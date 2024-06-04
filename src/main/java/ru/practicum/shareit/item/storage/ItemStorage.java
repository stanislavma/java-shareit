package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Item add(Item item);

    Item update(Item item);

    Optional<Item> getById(Long id);

    List<Item> getItemsByOwnerId(Long ownerId);

    List<Item> getItemsByText(String text);

    void delete(Long id);

}