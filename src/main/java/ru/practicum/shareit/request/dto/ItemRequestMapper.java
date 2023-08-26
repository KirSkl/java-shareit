package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.stream.Collectors;

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

    public static ItemRequestDtoResponseWithAnswers toItemRequestDtoResponseWithAnswers(ItemRequest itemRequest,
                                                                                        ItemRepository itemRepository) {

        return new ItemRequestDtoResponseWithAnswers(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemRepository.findAllByRequestId(
                                itemRequest.getId()).stream().map(ItemMapper::toItemRequestAnswerDto)
                        .collect(Collectors.toList())
        );
    }
}
