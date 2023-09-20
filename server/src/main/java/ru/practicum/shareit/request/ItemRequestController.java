package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Constants;
import ru.practicum.shareit.common.PaginationUtil;
import ru.practicum.shareit.common.Validator;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponseWithAnswers;

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
                                             @RequestBody ItemRequestDto itemRequestDto) {
        validator.checkIsUserExists(userId);
        return itemRequestService.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoResponseWithAnswers> getMyRequests(@RequestHeader(Constants.USER_HEADER) Long userId) {
        validator.checkIsUserExists(userId);
        return itemRequestService.getMyRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponseWithAnswers findItemRequest(@PathVariable Long requestId,
                                                             @RequestHeader(Constants.USER_HEADER) Long userId) {
        validator.checkIsUserExists(userId);
        return itemRequestService.findItemRequest(requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponseWithAnswers> getAllRequests(
            @RequestParam(defaultValue = Constants.DEFAULT_FROM) int from,
            @RequestParam(defaultValue = Constants.DEFAULT_SIZE) int size,
            @RequestHeader(Constants.USER_HEADER) Long userId) {
        validator.checkIsUserExists(userId);
        int page = PaginationUtil.positionToPage(from, size);
        return itemRequestService.getAll(page, size, userId);
    }
}
