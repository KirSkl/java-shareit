package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    @NotBlank
    private String description;
    private LocalDateTime created;

    @JsonCreator
    public ItemRequestDto(String description) {
        this.description = description;
        created = LocalDateTime.now();
    }
}
