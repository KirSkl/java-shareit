package ru.practicum.shareit.item;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Data
public class ItemInMemoryStorage implements ItemStorage {
    private final HashMap<Long, Item> items;
    private Long id = 0L;

    @Override
    public Item addItem(Item item) {
        item.setId(itemGenerateId());
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item editItem(Item item) {
        checkItemExists(item.getId());
        if (!items.get(item.getId()).getOwner().equals(item.getOwner())) {
            throw new NotOwnerException("Редактировать данные может только владелец вещи");
        }
        final var oldItem = items.get(item.getId());
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        return items.get(item.getId());
    }

    @Override
    public Item showItemInfo(Long itemId) {
        checkItemExists(itemId);
        return items.get(itemId);
    }

    @Override
    public List<Item> findAllMyItems(Long userId) {
        return items.values().stream().filter(item -> item.getOwner().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        String searchText = text.toLowerCase().strip();
        return items.values().stream().filter(item -> item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText) && item.getAvailable().equals(true))
                        .collect(Collectors.toList());
    }

    private long itemGenerateId() {
        return ++id;
    }

    private void checkItemExists(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException(String.format("Вещь с Id = %s не найдена", itemId));
        }
    }
}
