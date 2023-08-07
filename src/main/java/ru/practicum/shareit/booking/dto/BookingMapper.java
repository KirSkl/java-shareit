package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public final class BookingMapper {

    public static BookingDtoResponse toBookingDtoResponse(Booking booking) {
        return new BookingDtoResponse(
                booking.getId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getStatus(),
                booking.getBooker(),
                booking.getItem()
        );
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {
        return new BookingItemDto(
                booking.getId(),
                booking.getBooker().getId()
        );
    }

    public static Booking toBooking(BookingDtoRequest bookingDtoRequest, User booker, Item item) {
        return new Booking(
                null,
                bookingDtoRequest.getStart(),
                bookingDtoRequest.getEnd(),
                item,
                booker,
                BookingStatus.WAITING
        );
    }
}
