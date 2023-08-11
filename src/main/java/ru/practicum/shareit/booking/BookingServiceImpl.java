package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStates;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    @Override
    public BookingDtoResponse createBooking(Long userId, BookingDtoRequest bookingDtoRequest) {
        var booker = checkUserExistsAndGet(userId);
        var item = itemRepository.findById(bookingDtoRequest.getItemId()).orElseThrow(()
                -> new NotFoundException("Вещь не найдена"));
        if (!item.getIsAvailable()) {
            throw new ItemNotAvailable(String.format("Вещь с id = %s не доступна для бронирования",
                    bookingDtoRequest.getItemId()));
        }
        if (item.getOwnerId().equals(userId)) {
            throw new NotAccessException("Нельзя взять в аренду свою вещь");
        }
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(
                BookingMapper.toBooking(bookingDtoRequest, booker, item)));
    }

    @Override
    public BookingDtoResponse approvedBooking(Long bookingId, Boolean approved, Long userId) {
        checkUserExistsAndGet(userId);
        var booking = checkBookingExistsAndGet(bookingId);
        if (!booking.getItem().getOwnerId().equals(userId)) {
            throw new NotAccessException("Ответить на запрос аренды может только владелец вещи");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingAlreadyApprovedException("Нельзя изменить статус после одобрения");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoResponse getBooking(Long userId, Long bookingId) {
        checkUserExistsAndGet(userId);
        var booking = checkBookingExistsAndGet(bookingId);
        if (!userId.equals(booking.getBooker().getId()) &&
                !userId.equals(booking.getItem().getOwnerId())) {
            throw new NotFoundException("Информация о бронировании доступна только владельцу или арендатору");
        }
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public List<BookingDtoResponse> getAllBookings(Long userId, String state) {
        checkUserExistsAndGet(userId);
        try {
            switch (BookingStates.valueOf(state)) {
                case ALL:
                    return bookingRepository.findAllByBookerIdOrderByStartDateDesc(userId).stream()
                            .map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
                case CURRENT:
                    return bookingRepository.findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                                    userId, LocalDateTime.now(), LocalDateTime.now()).stream()
                            .map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
                case PAST:
                    return bookingRepository.findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(
                                    userId, LocalDateTime.now()).stream().map(BookingMapper::toBookingDtoResponse)
                            .collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(
                                    userId, LocalDateTime.now()).stream().map(BookingMapper::toBookingDtoResponse)
                            .collect(Collectors.toList());
                default:
                    return bookingRepository.findAllByBookerIdAndStatusOrderByStartDateDesc(userId,
                                    BookingStatus.valueOf(state)).stream()
                            .map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
            }
        } catch (IllegalArgumentException e) {
            throw new UnsupportedBookingStateException(String.format("Указан неподдерживаемый статус = %s", state));
        }
    }

    @Override
    public List<BookingDtoResponse> getAllItemBookings(Long userId, String state) {
        checkUserExistsAndGet(userId);
        try {
            switch (BookingStates.valueOf(state)) {
                case ALL:
                    return bookingRepository.findAllByItemOwnerIdOrderByStartDateDesc(userId).stream()
                            .map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
                case CURRENT:
                    return bookingRepository.findAllByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                                    userId, LocalDateTime.now(), LocalDateTime.now()).stream().
                            map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
                case PAST:
                    return bookingRepository.findAllByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(userId,
                                    LocalDateTime.now()).stream().map(BookingMapper::toBookingDtoResponse)
                            .collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.findAllByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(userId,
                                    LocalDateTime.now()).stream().map(BookingMapper::toBookingDtoResponse)
                            .collect(Collectors.toList());
                default:
                    return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDateDesc(userId,
                                    BookingStatus.valueOf(state)).stream().map(BookingMapper::toBookingDtoResponse)
                            .collect(Collectors.toList());
            }
        } catch (IllegalArgumentException e) {
            throw new UnsupportedBookingStateException(String.format("Указан неподдерживаемый статус = %s", state));
        }
    }

    private User checkUserExistsAndGet(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь на найден"));
    }

    private Booking checkBookingExistsAndGet(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(
                "Бронирование не найдено"));
    }
}
