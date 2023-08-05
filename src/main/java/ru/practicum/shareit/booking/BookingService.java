package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoPatchResponse;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingStates;

import java.util.List;

public interface BookingService {

    BookingDtoResponse createBooking(Long userId, BookingDtoRequest bookingDtoRequest);

    BookingDtoResponse approvedBooking(Long bookingId, Boolean approved, Long userId);

    BookingDtoResponse getBooking(Long userId, Long bookingId);

    List<BookingDtoResponse> getAllBookings(Long userId, BookingStates bookingStates);
}
