package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemStorage {
    Item addItem(Item item);

    Item editItem(Item item);
}
