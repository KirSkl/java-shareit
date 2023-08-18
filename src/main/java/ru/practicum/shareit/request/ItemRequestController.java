package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Constants;
import ru.practicum.shareit.common.Validator;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponseOwner;

import javax.validation.Valid;
import java.util.List;


@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private Validator validator;
    private ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoResponse addRequest(@RequestHeader(Constants.USER_HEADER) Long userId,
                                             @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info(String.format(
                "Получен запрос POST /requests на добавление нового запроса вещи от пользователя с id = %s", userId));
        validator.validateId(userId);
        return itemRequestService.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoResponseOwner> getMyRequests(@RequestHeader(Constants.USER_HEADER) Long userId) {
        log.info(String.format(
                "Получен запрос GET /requests на получение списка своих запросов от пользователя с id = %s", userId));
        validator.validateId(userId);
        return itemRequestService.getMyRequests(userId);
    }
}
