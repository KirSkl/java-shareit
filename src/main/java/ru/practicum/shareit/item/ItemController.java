package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Validator;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwner;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private ItemService itemService;
    private Validator validator;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info(String.format("Получен запрос POST /items на добавление вещи с названием %s", itemDto.getName()));
        validator.validateId(userId);
        validator.checkIsUserExists(userId);
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        log.info(String.format("Получен запрос PATCH /items/itemId=%s на редактирование данных вещи с названием %s",
                userId, itemDto.getName()));
        validator.validateId(userId);
        validator.validateId(itemId);
        validator.checkIsUserExists(userId);
        return itemService.editItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto showItemInfo(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(String.format("Получен запрос GET /items/itemId=%s на получение информации о вещи", itemId));
        validator.validateId(itemId);
        validator.validateId(userId);
        return itemService.showItemInfo(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> findAllMyItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(String.format("Получен запрос GET /items на просмотр списка вещей пользователя с id=%s", userId));
        validator.validateId(userId);
        validator.checkIsUserExists(userId);
        return itemService.findAllMyItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info(String.format(
                "Получен запрос GET /items/search на поиск вещей, соодержащих в названии или описании %s", text));
        return itemService.search(text);
    }

}
