package ru.practicum.shareit.exceptions;

public class InvalidPageParamsException extends RuntimeException {

    public InvalidPageParamsException(String message) {
        super(message);
    }
}
