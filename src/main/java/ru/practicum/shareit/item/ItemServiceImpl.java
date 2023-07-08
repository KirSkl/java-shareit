package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public ItemDto showItemInfo(Long itemId) {
        return ItemMapper.toItemDto(itemStorage.showItemInfo(itemId));
    }

    @Override
    public List<ItemDto> findAllMyItems(Long userId) {
        final var itemsDto = new ArrayList<ItemDto>();
        itemStorage.findAllMyItems(userId).stream().forEach(item -> itemsDto.add(ItemMapper.toItemDto(item)));
        return itemsDto;
    }

    @Override
    public List<ItemDto> search(String text) {
        final var searchResult = new ArrayList<ItemDto>();
        itemStorage.search(text).stream().forEach(item -> searchResult.add(ItemMapper.toItemDto(item)));
        return searchResult;
    }
}
