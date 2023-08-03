package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@AllArgsConstructor
@Data
public class BookingDto {
    @NotNull
    private Long itemId;
    @FutureOrPresent
    private Instant startDate;
    @FutureOrPresent
    private Instant endDate;
}
