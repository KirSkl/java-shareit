package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto editItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDto showItemInfo(Long itemId, Long userId);

    List<ItemDto> findAllMyItems(Long userId);

    List<ItemDto> search(String text);
}
