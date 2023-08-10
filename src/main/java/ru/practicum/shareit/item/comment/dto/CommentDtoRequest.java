package ru.practicum.shareit.item.comment.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CommentDtoRequest {
    @NotBlank
    private String text;
    private LocalDateTime created;

    @JsonCreator
    public CommentDtoRequest(String text) {
        this.text = text;
        created = LocalDateTime.now();
    }
}
