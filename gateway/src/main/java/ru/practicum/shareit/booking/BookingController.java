package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.common.Constants;
import ru.practicum.shareit.common.Validator;
import ru.practicum.shareit.exceptions.UnsupportedBookingStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private final Validator validator;

    @PostMapping
    public ResponseEntity<Object> createBooking(@Positive @RequestHeader(Constants.USER_HEADER) Long userId,
                                                @Valid @RequestBody BookingDtoRequest bookingDtoRequest) {
        log.info(String.format("Получен запрос POST /bookings на бронирование от пользователя с id = %s", userId));
        validator.validateBookingDto(bookingDtoRequest);
        return bookingClient.createBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approvedBooking(@Positive @PathVariable Long bookingId,
                                                  @RequestParam Boolean approved,
                                                  @Positive @RequestHeader(Constants.USER_HEADER) Long userId) {
        log.info(String.format("Получен запрос PATCH /bookingId = %s на изменение статуса бронирования  от " +
                "пользователя id = %s", bookingId, userId));
        return bookingClient.approvedBooking(bookingId, approved, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@Positive @RequestHeader(Constants.USER_HEADER) long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(defaultValue = Constants.DEFAULT_FROM)
                                              Integer from,
                                              @Positive @RequestParam(defaultValue = Constants.DEFAULT_SIZE)
                                              Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new UnsupportedBookingStateException("Unknown state: " + stateParam));
        log.info(String.format("Получен запрос GET/bookings на получение %s бронирований пользователя с id = %s, " +
                "начиная с бронирования %s, по %s бронирований на странице", state, userId, from, size));
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@Positive @RequestHeader(Constants.USER_HEADER) long userId,
                                             @Positive @PathVariable Long bookingId) {
        log.info(String.format("Получен запрос GET/bookingId = %s на получение информации о бронировании от " +
                "пользователя id = %s", bookingId, userId));
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllItemBookings(@Positive @RequestHeader(Constants.USER_HEADER) Long userId,
                                                     @RequestParam(name = "state", defaultValue = "ALL")
                                                     String stateParam,
                                                     @RequestParam(defaultValue = Constants.DEFAULT_FROM)
                                                     @PositiveOrZero int from,
                                                     @RequestParam(defaultValue = Constants.DEFAULT_SIZE)
                                                     @Positive int size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new UnsupportedBookingStateException("Unknown state: " + stateParam));
        log.info(String.format("Получен запрос GET/owner на получение %s бронирований вещей пользователя с id = %s, " +
                "начиная с бронирования %s, по %s бронирований на странице", state, userId, from, size));
        return bookingClient.getAllItemBookings(userId, state, from, size);
    }
}
