package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestAnswerDto;

import java.util.List;

@UtilityClass
public final class ItemMapper {

    public static ItemDto toItemDto(Item item, List<CommentDtoResponse> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getRequestId(),
                null,
                null,
                comments
        );
    }

    public static Item toItem(Long userId, ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                userId,
                itemDto.getRequestId()
        );
    }

    public static ItemRequestAnswerDto toItemRequestAnswerDto(Item item) {
        return new ItemRequestAnswerDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getRequestId()
        );
    }
}
