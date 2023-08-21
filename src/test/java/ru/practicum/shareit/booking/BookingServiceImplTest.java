package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.ItemNotAvailable;
import ru.practicum.shareit.exceptions.NotAccessException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    BookingServiceImpl bookingService;

    private final int from = 0;
    private final int size = 10;
    private Item item;
    private Item itemAnotherOwner;
    private ItemDto itemDto;
    private ItemDto itemAnotherOwnerDto;
    private ItemDto itemDtoWithoutBookings;
    private ItemDto itemDtoWithoutBookingsAndComments;
    private Long itemId;
    private Long userId;
    private User user;
    private Comment comment;
    private Comment commentWithoutId;
    private CommentDtoRequest commentDtoRequest;
    private CommentDtoResponse commentDtoResponse;
    private Booking booking;
    private Booking bookingAnother;
    private Booking bookingWithoutId;
    private Booking bookingAnotherItemWithoutId;
    private BookingDtoRequest bookingDtoRequest;
    private BookingDtoResponse bookingDtoResponse;
    private BookingDtoResponse bookingDtoResponseApproved;
    @BeforeEach
    void loadInitial() {
        item = new Item(1L, "Hammer", "Very big", true, 1L, 1L);
        itemAnotherOwner = new Item(2L, "Hammer", "Very big", true, 2L, 1L);
        userId = 1L;
        itemId = 1L;
        user = new User(1L, "John", "John@mail.com");
        comment = new Comment(1L, "Хорошая вещь", item, user, LocalDateTime.now());
        commentWithoutId = new Comment(null, "Хорошая вещь", item, user, LocalDateTime.now());
        commentDtoRequest = new CommentDtoRequest("Хорошая вещь");
        commentDtoResponse = new CommentDtoResponse(comment.getId(), comment.getText(),
                comment.getAuthor().getName(), comment.getCreated());
        booking = new Booking(1L, LocalDateTime.of(2000, 4, 27, 12, 1, 1),
                LocalDateTime.now(), item, user, BookingStatus.WAITING);
        bookingWithoutId = new Booking(1L, LocalDateTime.of(2000, 4, 27, 12, 1, 1),
                LocalDateTime.now(), item, user, BookingStatus.APPROVED);
        bookingAnother = new Booking(2L, LocalDateTime.now(),
                LocalDateTime.of(2035, 1, 1, 1, 1, 1),
                item, user, BookingStatus.APPROVED);
        bookingAnotherItemWithoutId = new Booking(null,
                LocalDateTime.of(2000, 4, 27, 12, 1, 1), LocalDateTime.now(),
                itemAnotherOwner, user, BookingStatus.WAITING);
        bookingDtoRequest = new BookingDtoRequest(itemId, booking.getStartDate(), booking.getEndDate());
        bookingDtoResponse = new BookingDtoResponse(booking.getId(), booking.getStartDate(), booking.getEndDate(),
                booking.getStatus(), booking.getBooker(), booking.getItem());
        bookingDtoResponseApproved = new BookingDtoResponse(booking.getId(), booking.getStartDate(), booking.getEndDate(),
                BookingStatus.APPROVED, booking.getBooker(), booking.getItem());
        BookingItemDto bookingItemDto = new BookingItemDto(booking.getId(), userId);
        BookingItemDto bookingItemDtoAnother = new BookingItemDto(bookingAnother.getId(), userId);
        itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getIsAvailable(),
                item.getRequestId(), bookingItemDto, bookingItemDtoAnother, List.of(commentDtoResponse));
        itemDtoWithoutBookings = new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getIsAvailable(),
                item.getRequestId(), null, null, List.of(commentDtoResponse));
        itemDtoWithoutBookingsAndComments = new ItemDto(item.getId(), item.getName(), item.getDescription(),
                item.getIsAvailable(), item.getRequestId(), null, null, Collections.emptyList());
        itemAnotherOwnerDto = new ItemDto(itemAnotherOwner.getId(), itemAnotherOwner.getName(),
                itemAnotherOwner.getDescription(), itemAnotherOwner.getIsAvailable(), itemAnotherOwner.getRequestId(),
                null, null, Collections.emptyList());
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
        verify(bookingRepository, times(1)).save(bookingAnotherItemWithoutId);
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

        var result = bookingService.approvedBooking(booking.getId(), true, userId);

        assertEquals(bookingDtoResponseApproved, result);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).findById(booking.getId());
        verify(bookingRepository, times(1)).save(booking);
    }
}
