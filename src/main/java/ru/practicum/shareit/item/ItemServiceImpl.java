package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotAccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(userId, itemDto)));
    }

    @Override
    public ItemDto editItem(Long userId, Long itemId, ItemDto itemDto) {
        final var oldItem = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь c id = %s не найдена", itemId)));
        if (!(oldItem.getOwnerId().equals(userId))) {
            throw new NotAccessException("Редактировать данные может только владелец вещи");
        }
        final var item = ItemMapper.toItem(userId, itemDto);
        if (item.getName() != null  && !item.getName().isBlank()) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getIsAvailable() != null) {
            oldItem.setIsAvailable(item.getIsAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(oldItem));
    }

    @Override
    public ItemDto showItemInfo(Long itemId) {
        return ItemMapper.toItemDto(itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с id = %s не найдена")));
    }

    @Override
    public List<ItemDto> findAllMyItems(Long userId) {
        return itemRepository.findItemsByOwnerId(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text)
                .stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
