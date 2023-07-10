package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private ItemStorage itemStorage;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        return ItemMapper.toItemDto(itemStorage.addItem(ItemMapper.toItem(userId, itemDto)));
    }

    @Override
    public ItemDto editItem(Long userId, Long itemId, ItemDto itemDto) {
        itemDto.setId(itemId);
        return ItemMapper.toItemDto(itemStorage.editItem(ItemMapper.toItem(userId, itemDto)));
    }

    @Override
    public ItemDto showItemInfo(Long itemId) {
        return ItemMapper.toItemDto(itemStorage.showItemInfo(itemId));
    }

    @Override
    public List<ItemDto> findAllMyItems(Long userId) {
        return itemStorage.findAllMyItems(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemStorage.search(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
