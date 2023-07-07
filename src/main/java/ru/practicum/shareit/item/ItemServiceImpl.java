package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    ItemStorage itemStorage;
    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        return ItemMapper.toItemDto(itemStorage.addItem(ItemMapper.toItem(userId, itemDto)));
    }

    @Override
    public ItemDto editItem(Long userId, Long itemId, ItemDto itemDto) {
        itemDto.setId(itemId);
        return ItemMapper.toItemDto(itemStorage.editItem(ItemMapper.toItem(userId, itemDto)));
    }
}
