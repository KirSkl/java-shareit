package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

@UtilityClass
public final class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getBookerId(),
                booking.getStartDate(),
                booking.getEndDate()
        );
    }

    public static Booking toBooking(BookingDto bookingDto, Long bookerId, Long ownerId) {
        return new Booking(
                null,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getItemId(),
                bookerId,
                BookingStatus.WAITING
        );
    }
}
