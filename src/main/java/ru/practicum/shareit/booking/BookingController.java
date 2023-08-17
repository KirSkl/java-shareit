package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.common.Constants;
import ru.practicum.shareit.common.Validator;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {

    private Validator validator;
    private BookingService bookingService;

    @PostMapping
    public BookingDtoResponse createBooking(@RequestHeader(Constants.USER_HEADER) Long userId,
                                            @Valid @RequestBody BookingDtoRequest bookingDtoRequest) {
        log.info(String.format("Получен запрос POST /bookings на бронирование от пользователя с id = %s", userId));
        validator.validateId(userId);
        validator.validateBookingDto(bookingDtoRequest);
        return bookingService.createBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse approvedBooking(@PathVariable Long bookingId, @RequestParam Boolean approved,
                                              @RequestHeader(Constants.USER_HEADER) Long userId) {
        log.info(String.format("Получен запрос PATCH /bookingId = %s на изменение статуса бронирования  от " +
                "пользователя id = %s", bookingId, userId));
        validator.validateId(userId);
        validator.validateId(bookingId);
        return bookingService.approvedBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(@RequestHeader(Constants.USER_HEADER) Long userId,
                                         @PathVariable Long bookingId) {
        log.info(String.format("Получен запрос GET/bookingId = %s на получение информации о бронировании от " +
                "пользователя id = %s", bookingId, userId));
        validator.validateId(userId);
        validator.validateId(bookingId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoResponse> getAllBookings(@RequestHeader(Constants.USER_HEADER) Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        log.info(String.format("Получен запрос GET/bookings на получение всех бронирований пользователя с id = %s",
                userId));
        validator.validateId(userId);
        return bookingService.getAllBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllItemBookings(@RequestHeader(Constants.USER_HEADER) Long userId,
                                                       @RequestParam(defaultValue = "ALL") String state) {
        log.info(String.format("Получен запрос GET/owner на получение всех бронирований вещей пользователя с id = %s",
                userId));
        validator.validateId(userId);
        return bookingService.getAllItemBookings(userId, state);
    }
}
