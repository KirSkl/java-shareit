package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoPatchResponse;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.common.Validator;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor //переделать букинг дто основываясь на тестах
public class BookingController {

    private Validator validator;
    private BookingService bookingService;

    @PostMapping
    public BookingDtoResponse createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Valid @RequestBody BookingDtoRequest bookingDtoRequest) {
        log.info(String.format("Получен запрос на бронирование от пользователя с id = %s", userId));
        validator.validateId(userId);
        validator.validateBookingDto(bookingDtoRequest);
        return bookingService.createBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse approvedBooking(@PathVariable Long bookingId, @RequestParam Boolean approved,
                                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(String.format("Получен запрос на изменение статуса бронирования id = %s от пользователя" +
                "id = %s", bookingId, userId));
        validator.validateId(userId);
        validator.validateId(bookingId);
        return bookingService.approvedBooking(bookingId, approved, userId);
    }
}
