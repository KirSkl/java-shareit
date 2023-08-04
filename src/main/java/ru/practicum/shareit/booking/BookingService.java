package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoPatchResponse;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

public interface BookingService {

    BookingDtoResponse createBooking(Long userId, BookingDtoRequest bookingDtoRequest);

    BookingDtoResponse approvedBooking(Long bookingId, Boolean approved, Long userId);
}
