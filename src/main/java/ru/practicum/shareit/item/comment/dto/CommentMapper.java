package ru.practicum.shareit.item.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.comment.model.Comment;

@UtilityClass
public final class CommentMapper {

    public static Comment toComment(CommentDtoRequest commentDtoRequest, Long userId, Long itemId) {
        return new Comment(
                null,
                commentDtoRequest.getText(),
                itemId,
                userId,
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
