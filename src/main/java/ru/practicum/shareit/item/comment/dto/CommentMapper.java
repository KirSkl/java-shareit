package ru.practicum.shareit.item.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public final class CommentMapper {

    public static Comment toComment(CommentDtoRequest commentDtoRequest, User user, Item item) {
        return new Comment(
                null,
                commentDtoRequest.getText(),
                item,
                user,
                commentDtoRequest.getCreated()
        );
    }

    public static CommentDtoResponse toCommentDtoResponse(Comment comment, String authorName) {
        return new CommentDtoResponse(
                comment.getId(),
                comment.getText(),
                authorName,
                comment.getCreated()
        );
    }
}
