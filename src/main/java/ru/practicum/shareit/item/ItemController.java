package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Validator;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private ItemService itemService;
    private Validator validator;

    @PostMapping
    public ItemDto addItem(@RequestHeader ("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        validator.validateId(userId);
        validator.checkIsUserExists(userId);
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@RequestHeader ("X-Sharer-User-Id") Long userId, @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        validator.validateId(userId);
        validator.validateId(itemId);
        validator.checkIsUserExists(userId);
        return itemService.editItem(userId, itemId, itemDto);
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
