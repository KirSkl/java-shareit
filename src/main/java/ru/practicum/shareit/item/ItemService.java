package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemService {

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto editItem(Long userId, Long itemId, ItemDto itemDto);
}
