package ru.practicum.shareit.common;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.exceptions.InvalidBookingDates;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationIdException;
import ru.practicum.shareit.user.UserRepository;

@Component
@Slf4j
@AllArgsConstructor
public class Validator {
    @Autowired
    private UserRepository userRepository;

    public void validateId(Long id) {
        log.info("Проверка id...");
        if (id < 1) {
            log.info("Ошибочный запрос - такой ID не может существовать");
            throw new ValidationIdException("ID меньше 1");
        }
    }

    public void validateBookingDto(BookingDtoRequest bookingDtoRequest) {
        log.info("Проверка дат бронирования...");
        if(bookingDtoRequest.getStart().isAfter(bookingDtoRequest.getEnd()) ||
                bookingDtoRequest.getStart().equals(bookingDtoRequest.getEnd())) {
            throw new InvalidBookingDates("Окончание бронирования должно быть после начала бронирования");
        }
    }

    public void checkIsUserExists(Long id) {
        log.info("Проверка наличия пользователя...");
        userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}
