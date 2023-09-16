package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum BookingStates {
    @JsonProperty
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED
}
