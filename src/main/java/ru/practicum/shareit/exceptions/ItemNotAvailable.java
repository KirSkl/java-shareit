package ru.practicum.shareit.exceptions;

public class ItemNotAvailable extends RuntimeException {

    public ItemNotAvailable(String message) {
        super(message);
    }
}
