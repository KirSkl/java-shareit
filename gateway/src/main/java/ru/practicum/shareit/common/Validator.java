package ru.practicum.shareit.common;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.exceptions.InvalidBookingDates;

@Component
@Slf4j
@AllArgsConstructor
public class Validator {

    public void validateBookingDto(BookingDtoRequest bookingDtoRequest) {
        log.info("Проверка дат бронирования...");
        if (bookingDtoRequest.getStart().isAfter(bookingDtoRequest.getEnd()) ||
                bookingDtoRequest.getStart().equals(bookingDtoRequest.getEnd())) {
            throw new InvalidBookingDates("Окончание бронирования должно быть после начала бронирования");
        }
    }
}
