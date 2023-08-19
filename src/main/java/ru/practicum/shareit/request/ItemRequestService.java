package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponseWithAnswers;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoResponse addRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoResponseWithAnswers> getMyRequests(Long userId);

    ItemRequestDtoResponseWithAnswers findItemRequest(Long requestId, Long userId);

    List<ItemRequestDtoResponseWithAnswers> getAll(int from, int size, Long userId);
}
