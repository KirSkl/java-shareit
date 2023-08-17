package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

public interface ItemRequestService {
    ItemRequestDtoResponse addRequest(Long userId, ItemRequestDto itemRequestDto);
}
