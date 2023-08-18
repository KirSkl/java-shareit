package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponseOwner;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoResponse addRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoResponseOwner> getMyRequests(Long userId);
}
