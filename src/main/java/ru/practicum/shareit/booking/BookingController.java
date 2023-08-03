package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.common.Validator;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    Validator validator;
    BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody @Valid BookingDto bookingDto) {
        log.info(String.format("Получен запрос на бронирование от пользователя с id = %s", userId));
        validator.validateId(userId);
        return bookingService.createBooking(userId, bookingDto);
    }
}
