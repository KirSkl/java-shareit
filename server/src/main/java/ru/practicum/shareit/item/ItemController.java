package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Constants;
import ru.practicum.shareit.common.PaginationUtil;
import ru.practicum.shareit.common.Validator;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private ItemService itemService;
    private Validator validator;

    @PostMapping
    public ItemDto addItem(@RequestHeader(Constants.USER_HEADER) Long userId, @RequestBody ItemDto itemDto) {
        validator.checkIsUserExists(userId);
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@RequestHeader(Constants.USER_HEADER) Long userId,
                            @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        validator.checkIsUserExists(userId);
        return itemService.editItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto showItemInfo(@PathVariable Long itemId, @RequestHeader(Constants.USER_HEADER) Long userId) {
        log.info(String.format("Получен запрос GET /items/itemId=%s на получение информации о вещи", itemId));
        return itemService.showItemInfo(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> findAllMyItems(@RequestHeader(Constants.USER_HEADER) Long userId, @RequestParam(
            defaultValue = Constants.DEFAULT_FROM) int from, @RequestParam(defaultValue = Constants.DEFAULT_SIZE)
                                        int size) {
        int page = PaginationUtil.positionToPage(from, size);
        return itemService.findAllMyItems(userId, page, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text, @RequestParam(defaultValue = Constants.DEFAULT_FROM)
    int from, @RequestParam(defaultValue = Constants.DEFAULT_SIZE) int size) {
        int page = PaginationUtil.positionToPage(from, size);
        return itemService.search(text, page, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse postComment(@PathVariable Long itemId, @RequestHeader(Constants.USER_HEADER) Long userId,
                                          @RequestBody CommentDtoRequest text) {
        return itemService.postComment(itemId, userId, text);
    }
}
