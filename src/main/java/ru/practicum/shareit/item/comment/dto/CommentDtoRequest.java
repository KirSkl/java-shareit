package ru.practicum.shareit.item.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CommentDtoRequest {
    @NotBlank
    private final String text;
    private LocalDateTime created = LocalDateTime.now();
}
