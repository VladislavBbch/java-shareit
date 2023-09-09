package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item create(Item item);

    List<Item> read(Long userId);

    int update(Item item);

    Optional<Item> getById(Long id);

    void delete(Long userId, Long id);

    List<Item> searchItems(Long userId, String text);
}