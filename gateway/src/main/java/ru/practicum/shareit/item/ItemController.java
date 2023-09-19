package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Constants;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@RestController
@Slf4j
@RequestMapping("/items")
@AllArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@Positive @RequestHeader(Constants.USER_HEADER) Long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        log.info(String.format("Получен запрос POST /items на добавление вещи с названием %s", itemDto.getName()));
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> editItem(@Positive @RequestHeader(Constants.USER_HEADER) Long userId,
                                           @Positive @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        log.info(String.format("Получен запрос PATCH /items/itemId=%s на редактирование данных вещи с названием %s",
                userId, itemDto.getName()));
        return itemClient.editItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> showItemInfo(@Positive @PathVariable Long itemId,
                                               @Positive @RequestHeader(Constants.USER_HEADER) Long userId) {
        log.info(String.format("Получен запрос GET /items/itemId=%s на получение информации о вещи", itemId));
        return itemClient.showItemInfo(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllMyItems(@Positive @RequestHeader(Constants.USER_HEADER) Long userId,
                                                 @PositiveOrZero @RequestParam(defaultValue = Constants.DEFAULT_FROM)
                                                 int from,
                                                 @Positive @RequestParam(defaultValue = Constants.DEFAULT_SIZE)
                                                 int size) {
        log.info(String.format("Получен запрос GET /items на просмотр списка вещей пользователя с id=%s, начиная с " +
                "вещи %s, по %s вещей на странице", userId, from, size));
        return itemClient.findAllMyItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @Positive @RequestHeader(Constants.USER_HEADER) Long userId,
                                         @RequestParam(defaultValue = Constants.DEFAULT_FROM) @PositiveOrZero int from,
                                         @RequestParam(defaultValue = Constants.DEFAULT_SIZE) @Positive int size) {
        log.info(String.format(
                "Получен запрос GET /items/search на поиск вещей, содержащих в названии или описании %s, начиная с " +
                        "вещи %s, по %s вещей на странице", text, from, size));
        if (text.isBlank() || text.isEmpty()) {
            return ResponseEntity.ok().body(Collections.emptyList());
        }
        return itemClient.search(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@Positive @PathVariable Long itemId,
                                              @Positive @RequestHeader(Constants.USER_HEADER) Long userId,
                                              @Valid @RequestBody CommentDtoRequest text) {
        log.info(String.format(
                "Получен запрос POST /items/userId=%s/comment на публикацию отзыва от пользователя с id = %s",
                itemId, userId));
        return itemClient.postComment(itemId, userId, text);
    }
}
