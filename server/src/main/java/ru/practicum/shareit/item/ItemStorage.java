package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item addItem(Item item);

    Item editItem(Item item);

    Item showItemInfo(Long itemId);

    List<Item> findAllMyItems(Long userId);

    List<Item> search(String text);
}
