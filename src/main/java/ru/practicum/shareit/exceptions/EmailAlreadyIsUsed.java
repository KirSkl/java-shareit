package ru.practicum.shareit.exceptions;

public class EmailAlreadyIsUsed extends RuntimeException {

    public EmailAlreadyIsUsed(String message) {
        super(message);
    }
}
