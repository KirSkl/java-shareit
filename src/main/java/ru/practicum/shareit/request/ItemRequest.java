package ru.practicum.shareit.request;

import lombok.Data;

@Data
public class ItemRequest {
    private Long id;
    private String description;
    private Long requestorId;
}
