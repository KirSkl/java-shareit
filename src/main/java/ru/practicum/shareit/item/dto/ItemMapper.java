package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@UtilityClass
public final class ItemMapper {

    public static ItemDto toItemDto(Item item, List<CommentDtoResponse> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getRequest(),
                null,
                null,
                comments
        );
    }

   /* public static ItemDtoOwner toItemDtoOwner(Item item, List<Comment> commentsList) {*//*
        return new ItemDtoOwner(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getRequest(),
                null,
                null,
                commentsList
        );*//*
    }*/

    public static Item toItem(Long userId, ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                userId,
                itemDto.getRequest()
        );
    }

}
