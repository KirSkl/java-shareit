package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Constants;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@Positive @RequestHeader(Constants.USER_HEADER) Long userId,
                                             @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info(String.format(
                "Получен запрос POST /requests на добавление нового запроса вещи от пользователя с id = %s", userId));
        return requestClient.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getMyRequests(
            @Positive @RequestHeader(Constants.USER_HEADER) Long userId) {
        log.info(String.format(
                "Получен запрос GET /requests на получение списка своих запросов от пользователя с id = %s", userId));
        return requestClient.getMyRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findItemRequest(@Positive @PathVariable Long requestId,
                                                  @Positive @RequestHeader(Constants.USER_HEADER) Long userId) {
        log.info(String.format("Получен запрос GET /requests/requestId=%s на получение информации о запросе",
                requestId));
        return requestClient.findItemRequest(requestId, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestParam(defaultValue = Constants.DEFAULT_FROM) @PositiveOrZero int from,
            @RequestParam(defaultValue = Constants.DEFAULT_SIZE) @Positive int size,
            @RequestHeader(Constants.USER_HEADER) Long userId) {
        log.info(String.format(
                "Получен запрос GET /requests/all от пользователя = %s на получение списка запросов других " +
                        "пользователей с параметрами пагинации от %s до %s", userId, from, size));
        return requestClient.getAll(from, size, userId);
    }
}
