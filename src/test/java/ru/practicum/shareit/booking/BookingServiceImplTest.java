package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    private final int from = 0;
    private final int size = 10;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    private Item item;
    private Item itemAnotherOwner;
    private Long userId;
    private User user;
    private Booking booking;
    private BookingDtoRequest bookingDtoRequest;
    private BookingDtoResponse bookingDtoResponse;
    private String stateAll;
    private String stateCurrent;
    private String statePast;
    private String stateFuture;
    private String stateWaiting;
    private String stateRejected;
    private String stateWrong;

    @BeforeEach
    void loadInitial() {
        item = new Item(1L, "Hammer", "Very big", true, 1L, 1L);
        itemAnotherOwner = new Item(2L, "Hammer", "Very big", true, 2L, 1L);
        userId = 1L;
        user = new User(1L, "John", "John@mail.com");
        booking = new Booking(1L, LocalDateTime.of(2000, 4, 27, 12, 1, 1),
                LocalDateTime.now(), item, user, BookingStatus.WAITING);
        bookingDtoRequest = new BookingDtoRequest(1L, booking.getStartDate(), booking.getEndDate());
        bookingDtoResponse = new BookingDtoResponse(booking.getId(), booking.getStartDate(), booking.getEndDate(),
                booking.getStatus(), booking.getBooker(), booking.getItem());
        stateAll = "ALL";
        stateCurrent = "CURRENT";
        stateFuture = "FUTURE";
        statePast = "PAST";
        stateRejected = "REJECTED";
        stateWaiting = "WAITING";
        stateWrong = "fjskfs";
    }

    @Test
    void testCreateBookingOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemAnotherOwner));
        when(bookingRepository.save(any())).thenReturn(booking);

        var result = bookingService.createBooking(userId, bookingDtoRequest);

        assertEquals(bookingDtoResponse, result);
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(bookingDtoRequest.getItemId());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void testCreateBookingUserNotExistsThrownNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(userId, bookingDtoRequest));

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCreateBookingItemNotExistsThrownNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(userId, bookingDtoRequest));

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(bookingDtoRequest.getItemId());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCreateBookingItemNotAvailableThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemAnotherOwner));
        itemAnotherOwner.setIsAvailable(false);

        assertThrows(ItemNotAvailable.class, () -> bookingService.createBooking(userId, bookingDtoRequest));

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(bookingDtoRequest.getItemId());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCreateBookingUserIsOwnerThrownNotAccess() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(NotAccessException.class, () -> bookingService.createBooking(userId, bookingDtoRequest));

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(bookingDtoRequest.getItemId());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testApprovedBookingOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        bookingDtoResponse.setStatus(BookingStatus.APPROVED);

        var result = bookingService.approvedBooking(booking.getId(), true, userId);

        assertEquals(bookingDtoResponse, result);

        bookingDtoResponse.setStatus(BookingStatus.REJECTED);
        booking.setStatus(BookingStatus.WAITING);

        result = bookingService.approvedBooking(booking.getId(), false, userId);

        assertEquals(bookingDtoResponse, result);
        verify(userRepository, times(2)).findById(userId);
        verify(bookingRepository, times(2)).findById(booking.getId());
        verify(bookingRepository, times(2)).save(booking);
    }

    @Test
    void testApprovedBookingUserNotExistsThrownNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approvedBooking(
                booking.getId(), true, userId));

        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testApprovedBookingNotExistsThrownNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approvedBooking(
                booking.getId(), true, userId));

        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findById(booking.getId());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testApprovedBookingAlreadyApprovedThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        booking.setStatus(BookingStatus.APPROVED);

        assertThrows(BookingAlreadyApprovedException.class, () -> bookingService.approvedBooking(
                booking.getId(), true, userId));
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findById(booking.getId());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testApprovedBookingThrownNotAccessExc() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        final Long notOwnerUserId = 2L;

        assertThrows(NotAccessException.class, () -> bookingService.approvedBooking(
                booking.getId(), true, notOwnerUserId));
        verify(userRepository, times(1)).findById(notOwnerUserId);
        verify(bookingRepository, times(1)).findById(booking.getId());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testGetBookingOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        var result = bookingService.getBooking(userId, booking.getId());

        assertEquals(bookingDtoResponse, result);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findById(booking.getId());
    }

    @Test
    void testGetBookingUserNotFoundThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(userId, booking.getId()));

        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, never()).findById(any());
    }

    @Test
    void testGetBookingUserNotOwnerThrownNotFound() {
        booking.setItem(itemAnotherOwner);
        user.setId(3L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(userId, booking.getId()));

        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findById(booking.getId());
    }

    @Test
    void testGetBookingNotExistsThrownNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(userId, booking.getId()));

        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findById(booking.getId());
    }

    @Test
    void testGetAllBookingsAllOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdOrderByStartDateDesc(anyLong(), any())).thenReturn(List.of(booking));

        var resultAll = bookingService.getAllBookings(userId, stateAll, from, size);

        assertEquals(List.of(bookingDtoResponse), resultAll);

        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1))
                .findAllByBookerIdOrderByStartDateDesc(anyLong(), any());
    }

    @Test
    void testGetAllBookingsCurrentOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                anyLong(), any(), any(), any())).thenReturn(List.of(booking));

        var resultCurrent = bookingService.getAllBookings(userId, stateCurrent, from, size);

        assertEquals(List.of(bookingDtoResponse), resultCurrent);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                anyLong(), any(), any(), any());
    }

    @Test
    void testGetAllBookingPastOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(
                anyLong(), any(), any())).thenReturn(List.of(booking));

        var resultPast = bookingService.getAllBookings(userId, statePast, from, size);

        assertEquals(List.of(bookingDtoResponse), resultPast);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(
                anyLong(), any(), any());
    }

    @Test
    void testGetAllBookingFutureOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(
                anyLong(), any(), any())).thenReturn(List.of(booking));

        var resultFuture = bookingService.getAllBookings(userId, stateFuture, from, size);

        assertEquals(List.of(bookingDtoResponse), resultFuture);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(
                anyLong(), any(), any());
    }

    @Test
    void testGetAllBookingRejectedOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDateDesc(
                anyLong(), any(), any())).thenReturn(List.of(booking));

        var resultRejected = bookingService.getAllBookings(userId, stateRejected, from, size);

        assertEquals(List.of(bookingDtoResponse), resultRejected);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findAllByBookerIdAndStatusOrderByStartDateDesc(
                anyLong(), any(), any());
    }

    @Test
    void testGetAllBookingWaitingOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDateDesc(
                anyLong(), any(), any())).thenReturn(List.of(booking));

        var resultWaiting = bookingService.getAllBookings(userId, stateWaiting, from, size);

        assertEquals(List.of(bookingDtoResponse), resultWaiting);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findAllByBookerIdAndStatusOrderByStartDateDesc(
                anyLong(), any(), any());
    }

    @Test
    void testGetAllBookingsWrongStateThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(UnsupportedBookingStateException.class, () ->
                bookingService.getAllBookings(userId, stateWrong, from, size));

        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, never())
                .findAllByBookerIdOrderByStartDateDesc(anyLong(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                anyLong(), any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(
                anyLong(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(
                anyLong(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndStatusOrderByStartDateDesc(
                anyLong(), any(), any());
    }

    @Test
    void testGetAllBookingUserNotExistsThrownNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getAllBookings(userId, stateAll, from, size));

        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, never()).findAllByBookerIdOrderByStartDateDesc(anyLong(), any());
    }

    @Test
    void testGetAllItemBookingsAllOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDateDesc(anyLong(), any())).thenReturn(List.of(booking));

        var resultAll = bookingService.getAllItemBookings(userId, stateAll, from, size);

        assertEquals(List.of(bookingDtoResponse), resultAll);

        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdOrderByStartDateDesc(userId, PageRequest.of(from, size));
    }

    @Test
    void testGetAllItemBookingsCurrentOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                anyLong(), any(), any(), any())).thenReturn(List.of(booking));

        var resultCurrent = bookingService.getAllItemBookings(userId, stateCurrent, from, size);

        assertEquals(List.of(bookingDtoResponse), resultCurrent);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                        anyLong(), any(), any(), any());
    }

    @Test
    void testGetAllItemBookingPastOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(
                anyLong(), any(), any())).thenReturn(List.of(booking));

        var resultPast = bookingService.getAllItemBookings(userId, statePast, from, size);

        assertEquals(List.of(bookingDtoResponse), resultPast);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(anyLong(), any(), any());
    }

    @Test
    void testGetAllItemBookingFutureOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(
                anyLong(), any(), any())).thenReturn(List.of(booking));

        var resultFuture = bookingService.getAllItemBookings(userId, stateFuture, from, size);

        assertEquals(List.of(bookingDtoResponse), resultFuture);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(anyLong(), any(), any());
    }

    @Test
    void testGetAllItemBookingRejectedOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDateDesc(
                anyLong(), any(), any())).thenReturn(List.of(booking));

        var resultRejected = bookingService.getAllItemBookings(userId, stateRejected, from, size);

        assertEquals(List.of(bookingDtoResponse), resultRejected);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatusOrderByStartDateDesc(
                userId, BookingStatus.valueOf(stateRejected), PageRequest.of(from, size));
    }

    @Test
    void testGetAllItemBookingWaitingOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDateDesc(
                anyLong(), any(), any())).thenReturn(List.of(booking));

        var resultWaiting = bookingService.getAllItemBookings(userId, stateWaiting, from, size);

        assertEquals(List.of(bookingDtoResponse), resultWaiting);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatusOrderByStartDateDesc(
                userId, BookingStatus.valueOf(stateWaiting), PageRequest.of(from, size));
    }

    @Test
    void testGetAllItemBookingUserNotExistsThrownNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getAllItemBookings(userId, stateAll, from, size));

        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, never()).findAllByItemOwnerIdOrderByStartDateDesc(anyLong(), any());
    }

    @Test
    void testGetAllItemBookingsWrongStateThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(UnsupportedBookingStateException.class, () ->
                bookingService.getAllItemBookings(userId, stateWrong, from, size));

        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, never()).findAllByItemOwnerIdOrderByStartDateDesc(anyLong(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                anyLong(), any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(
                anyLong(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(
                anyLong(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStatusOrderByStartDateDesc(anyLong(), any(), any());
    }
}
