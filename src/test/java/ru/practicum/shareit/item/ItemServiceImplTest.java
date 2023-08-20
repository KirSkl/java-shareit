package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.NotAccessException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

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
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    private Item item;
    private Item itemAnotherOwner;
    private ItemDto itemDto;
    private ItemDto itemAnotherOwnerDto;
    private Long userId;

    @BeforeEach
    void loadInitial() {
        item = new Item(1L, "Hammer", "Very big", true, 1L, 1L);
        itemAnotherOwner = new Item(2L, "Hammer", "Very big", true, 2L, 1L);
        itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getIsAvailable(),
                item.getRequestId(), null, null, Collections.emptyList());
        itemAnotherOwnerDto = new ItemDto(itemAnotherOwner.getId(), itemAnotherOwner.getName(),
                itemAnotherOwner.getDescription(), itemAnotherOwner.getIsAvailable(), itemAnotherOwner.getRequestId(),
                null, null, Collections.emptyList());
        userId = 1L;
    }

    @Test
    void testAddItemOk() {
        when(itemRepository.save(any())).thenReturn(item);

        var result = itemService.addItem(userId, itemDto);

        assertEquals(itemDto, result);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void testEditItemOk() {
        when(commentRepository.findAllByItem(any())).thenReturn(Collections.emptyList());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        var result = itemService.editItem(userId, item.getId(), itemDto);

        assertEquals(itemDto, result);
        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any());
        verify(commentRepository, times(1)).findAllByItem(any());
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
        when(commentRepository.findAllByItem(any())).thenReturn(Collections.emptyList());
        when(bookingRepository.findFirstBookingByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc(any(), any(),
                any())).thenReturn(Optional.empty());
        when(bookingRepository.findFirstBookingByItemIdAndStartDateAfterAndStatusNotOrderByStartDate(any(), any(),
                any())).thenReturn(Optional.empty());

        var result = itemService.showItemInfo(item.getId(), userId);

        assertEquals(itemDto, result);
        verify(bookingRepository, times(1)).
                findFirstBookingByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc(any(), any(), any());
        verify(bookingRepository, times(1)).
                findFirstBookingByItemIdAndStartDateAfterAndStatusNotOrderByStartDate(any(), any(), any());
        verify(itemRepository, times(1)).findById(anyLong());
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
        when(commentRepository.findAllByItem(any())).thenReturn(Collections.emptyList());
        when(bookingRepository.findFirstBookingByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc(any(), any(),
                any())).thenReturn(Optional.empty());
        when(bookingRepository.findFirstBookingByItemIdAndStartDateAfterAndStatusNotOrderByStartDate(any(), any(),
                any())).thenReturn(Optional.empty());
        final int from = 0;
        final int size = 10;

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
}
