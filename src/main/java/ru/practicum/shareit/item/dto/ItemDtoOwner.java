package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.model.Comment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoOwner {/*
    @Positive
    private Long id;
    @NotBlank(message = "Название не должно быть пустым")
    private String name;
    @NotBlank(message = "Описание не должно быть пустым")
    private String description;
    @NotNull
    private Boolean available;
    private long request;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<Comment> comments;*/
}
