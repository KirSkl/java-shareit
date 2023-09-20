package ru.practicum.shareit.exceptions;

public class InvalidBookingDates extends RuntimeException {

    public InvalidBookingDates(String message) {
        super(message);
    }
}
