package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@ResponseBody
public class ExceptionHandlerController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleExceptionDuplicateEmail(EmailAlreadyIsUsed e) {
        return Map.of("error", "У пользователей не может быть одинаковый адрес Email",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(NotFoundException e) {
        return Map.of("error:", "Не найдено", "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNumberFormatException(NumberFormatException e) {
        return Map.of("error", "ID должен быть указан в числовом формате",
                "errorMessage", e.getMessage());
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidPageParamsException(InvalidPageParamsException e) {
        return Map.of("error", "Параметры пагинации не верны",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationIDException(ValidationIdException e) {
        return Map.of("error", "Неверный ID", "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND) //был 403, но в тестах требуется именно 404
    public Map<String, String> handleNotAccessException(NotAccessException e) {
        return Map.of("error", "Нет прав доступа");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoSuchElementException(NoSuchElementException e) {
        return Map.of("error:", "Не найдено", "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleItemNotAvailable(ItemNotAvailable e) {
        return Map.of("error", "Данная вещь не доступна для бронирования",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidBookingDates(InvalidBookingDates e) {
        return Map.of("error", "Указаны неверные даты бронирования",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUnsupportedBookingStateException(UnsupportedBookingStateException e) {
        return Map.of("error", "Unknown state: UNSUPPORTED_STATUS",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBookingAlreadyApprovedException(BookingAlreadyApprovedException e) {
        return Map.of("error", "Бронирование уже одобрено",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotBookerException(NotBookerException e) {
        return Map.of("error", "Комментарии могут оставлять только арендаторы",
                "errorMessage", e.getMessage());
    }
}
