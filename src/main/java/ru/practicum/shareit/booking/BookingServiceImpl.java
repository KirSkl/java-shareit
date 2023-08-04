package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoPatchResponse;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.ItemNotAvailable;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    @Override
    public BookingDtoResponse createBooking(Long userId, BookingDtoRequest bookingDtoRequest) {
        var item = itemRepository.findById(bookingDtoRequest.getItemId()).get();
        var booker = userRepository.findById(userId).get();
        if (!item.getIsAvailable()) {
            throw new ItemNotAvailable(String.format("Вещь с id = %s не доступна для бронирования",
                    bookingDtoRequest.getItemId()));
        }
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(
                BookingMapper.toBooking(bookingDtoRequest, booker, item)));
    }

    @Override
    public BookingDtoResponse approvedBooking(Long bookingId, Boolean approved, Long userId) {
        var booking = bookingRepository.findById(bookingId).get();
        if (!booking.getItem().getOwnerId().equals(userId)) {
            throw new NotOwnerException("Ответить на запрос аренды может только владелец вещи");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDtoResponse(booking);
    }
}
