package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.NotAccessException;
import ru.practicum.shareit.exceptions.NotBookerException;
import ru.practicum.shareit.exceptions.NotFoundException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    private final int from = 0;
    private final int size = 10;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    private Item item;
    private Item itemAnotherOwner;
    private ItemDto itemDto;
    private ItemDto itemAnotherOwnerDto;
    private ItemDto itemDtoWithoutBookings;
    private ItemDto itemDtoWithoutBookingsAndComments;
    private Long userId;
    private User user;
    private Comment comment;
    private Comment commentWithoutId;
    private CommentDtoRequest commentDtoRequest;
    private CommentDtoResponse commentDtoResponse;
    private Booking booking;
    private Booking bookingAnother;

    @BeforeEach
    void loadInitial() {
        item = new Item(1L, "Hammer", "Very big", true, 1L, 1L);
        itemAnotherOwner = new Item(2L, "Hammer", "Very big", true, 2L, 1L);
        userId = 1L;
        user = new User(1L, "John", "John@mail.com");
        comment = new Comment(1L, "Хорошая вещь", item, user, LocalDateTime.now());
        commentWithoutId = new Comment(null, "Хорошая вещь", item, user, LocalDateTime.now());
        commentDtoRequest = new CommentDtoRequest("Хорошая вещь");
        commentDtoResponse = new CommentDtoResponse(comment.getId(), comment.getText(),
                comment.getAuthor().getName(), comment.getCreated());
        booking = new Booking(1L, LocalDateTime.of(2000, 4, 27, 12, 1, 1),
                LocalDateTime.now(), item, user, BookingStatus.APPROVED);
        bookingAnother = new Booking(2L, LocalDateTime.now(),
                LocalDateTime.of(2035, 1, 1, 1, 1, 1),
                item, user, BookingStatus.APPROVED);
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
    void testAddItemOk() {
        when(itemRepository.save(any())).thenReturn(item);

        var result = itemService.addItem(userId, itemDto);

        assertEquals(itemDtoWithoutBookingsAndComments, result);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void testEditItemOk() {
        when(commentRepository.findAllByItem(any())).thenReturn(List.of(comment));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        var result = itemService.editItem(userId, item.getId(), itemDto);

        assertEquals(itemDtoWithoutBookings, result);
        verify(itemRepository, times(1)).findById(item.getId());
        verify(itemRepository, times(1)).save(item);
        verify(commentRepository, times(1)).findAllByItem(item);
    }

    @Test
    void testEditItemThrownNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.editItem(userId, item.getId(), itemDto));
        verify(itemRepository, never()).save(any());
        verify(commentRepository, never()).findAllByItem(any());
    }

    @Test
    void testEditItemThrownNotAccess() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemAnotherOwner));

        assertThrows(NotAccessException.class, () -> itemService.editItem(userId, item.getId(), itemDto));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void testShowItemInfoOwnerOk() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItem(any())).thenReturn(List.of(comment));
        when(bookingRepository.findFirstBookingByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc(any(), any(),
                any())).thenReturn(Optional.of(booking));
        when(bookingRepository.findFirstBookingByItemIdAndStartDateAfterAndStatusNotOrderByStartDate(any(), any(),
                any())).thenReturn(Optional.of(bookingAnother));

        var result = itemService.showItemInfo(item.getId(), userId);

        assertEquals(itemDto, result);
        verify(bookingRepository, times(1)).
                findFirstBookingByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc(any(), any(), any());
        verify(bookingRepository, times(1)).
                findFirstBookingByItemIdAndStartDateAfterAndStatusNotOrderByStartDate(any(), any(), any());
        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    void testShowItemInfoNotOwnerOk() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemAnotherOwner));
        when(commentRepository.findAllByItem(any())).thenReturn(Collections.emptyList());

        var result = itemService.showItemInfo(item.getId(), userId);

        assertEquals(itemAnotherOwnerDto, result);
        verify(itemRepository, times(1)).findById(item.getId());
        verify(bookingRepository, never()).findFirstBookingByItemIdAndStartDateAfterAndStatusNotOrderByStartDate(
                any(), any(), any());
        verify(bookingRepository, never()).findFirstBookingByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc(
                any(), any(), any());
    }

    @Test
    void testShowItemInfoThrownNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.showItemInfo(item.getId(), userId));
        verify(commentRepository, never()).findAllByItem(any());
    }

    @Test
    void testFindAllMyItemsOk() {
        when(itemRepository.findItemsByOwnerIdOrderById(anyLong(), any())).thenReturn(List.of(item));
        when(commentRepository.findAllByItem(any())).thenReturn(List.of(comment));
        when(bookingRepository.findFirstBookingByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc(any(), any(),
                any())).thenReturn(Optional.of(booking));
        when(bookingRepository.findFirstBookingByItemIdAndStartDateAfterAndStatusNotOrderByStartDate(any(), any(),
                any())).thenReturn(Optional.of(bookingAnother));

        var result = itemService.findAllMyItems(userId, from, size);

        assertEquals(List.of(itemDto), result);
        verify(itemRepository, times(1)).findItemsByOwnerIdOrderById
                (userId, PageRequest.of(from, size));
        verify(commentRepository, times(1)).findAllByItem(item);
        verify(bookingRepository, times(1)).
                findFirstBookingByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc(any(), any(), any());
        verify(bookingRepository, times(1)).
                findFirstBookingByItemIdAndStartDateAfterAndStatusNotOrderByStartDate(any(), any(), any());
    }

    @Test
    void testFindAllMyItemsIfEmptyOk() {
        when(itemRepository.findItemsByOwnerIdOrderById(anyLong(), any())).thenReturn(Collections.emptyList());

        var result = itemService.findAllMyItems(userId, from, size);

        assertEquals(Collections.emptyList(), result);
        verify(itemRepository, times(1)).findItemsByOwnerIdOrderById(1L,
                PageRequest.of(from, size));
    }

    @Test
    void testSearchOk() {
        when(itemRepository.search(any(), any())).thenReturn(List.of(item));
        when(commentRepository.findAllByItem(any())).thenReturn(List.of(comment));

        final String text = "Very";
        var result = itemService.search(text, from, size);

        assertEquals(List.of(itemDtoWithoutBookings), result);
        verify(itemRepository, times(1)).search(text, PageRequest.of(0, 10));
        verify(commentRepository, times(1)).findAllByItem(item);
    }

    @Test
    void testSearchIfTextIsBlankOk() {
        var result = itemService.search("", from, size);

        assertEquals(Collections.emptyList(), result);
        verify(itemRepository, never()).search(any(), any());
    }

    @Test
    void testSearchIfWrongTextOk() {
        final String wrongText = "jfshksh";
        when(itemRepository.search(any(), any())).thenReturn(Collections.emptyList());

        var result = itemService.search(wrongText, from, size);

        assertEquals(Collections.emptyList(), result);
        verify(itemRepository, times(1)).search(wrongText, PageRequest.of(from, size));
        verify(commentRepository, never()).findAllByItem(any());
    }

    @Test
    void testPostCommentOk() {
        when(commentRepository.save(any())).thenReturn(comment);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndItemIdAndEndDateBeforeAndStatus(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));

        var result = itemService.postComment(item.getId(), userId, commentDtoRequest);

        assertEquals(commentDtoResponse, result);
        verify(commentRepository, times(1)).save(commentWithoutId);
        verify(itemRepository, times(1)).findById(item.getId());
        verify(userRepository, times(2)).findById(userId);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndItemIdAndEndDateBeforeAndStatus(userId, item.getId(),
                        commentDtoRequest.getCreated(), BookingStatus.APPROVED);
    }

    @Test
    void testPostCommentItemNotExistThrownNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.postComment(item.getId(), userId, commentDtoRequest));

        verify(commentRepository, never()).save(any());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(userRepository, never()).findById(any());
        verify(bookingRepository, never())
                .findAllByBookerIdAndItemIdAndEndDateBeforeAndStatus(any(), any(), any(), any());
    }

    @Test
    void testPostCommentUserNotExistThrownNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.postComment(item.getId(), userId, commentDtoRequest));

        verify(commentRepository, never()).save(any());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, never())
                .findAllByBookerIdAndItemIdAndEndDateBeforeAndStatus(any(), any(), any(), any());
    }

    @Test
    void testPostCommentUserNotBookerThrownNoBookerExc() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndItemIdAndEndDateBeforeAndStatus(anyLong(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        assertThrows(NotBookerException.class, () -> itemService.postComment(item.getId(), userId, commentDtoRequest));

        verify(commentRepository, never()).save(any());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndItemIdAndEndDateBeforeAndStatus(userId, item.getId(),
                        commentDtoRequest.getCreated(), BookingStatus.APPROVED);
    }
}
