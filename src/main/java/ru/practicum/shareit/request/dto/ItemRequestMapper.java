package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public final class ItemRequestMapper {

    public static ItemRequest toItemRequest(User user, ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                null,
                itemRequestDto.getDescription(),
                user,
                itemRequestDto.getCreated()
        );
    }

    public static ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest itemRequest) {
        return new ItemRequestDtoResponse(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }
}
