package ru.practicum.shareit.exceptions;

public class NotBookerException extends RuntimeException {
    public NotBookerException(String message) {
        super(message);
    }
}
