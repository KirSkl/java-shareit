package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    @PutMapping
    public void addItem(@RequestHeader ("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {

    }

    @PatchMapping("/{itemId}")
    public void editItem(@RequestHeader ("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {

    }

    @GetMapping("/{itemId}")
    public void showItemInfo(@PathVariable Long itemId) {

    }

    @GetMapping
    public void findAllMyItems(@RequestHeader("X-Sharer-User-Id") Long userId) {

    }

    @GetMapping("/search")
    public void search(@RequestParam String text) {

    }

}
