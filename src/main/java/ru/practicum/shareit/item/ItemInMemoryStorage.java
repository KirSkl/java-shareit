package ru.practicum.shareit.item;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;

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
        if (!items.containsKey(item.getId())) {
            throw new NotFoundException(String.format("Вещь с Id = %s не найдена", item.getId()));
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

    private long itemGenerateId() {
        return ++id;
    }
}
