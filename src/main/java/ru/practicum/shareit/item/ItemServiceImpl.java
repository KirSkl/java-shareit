package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.NotAccessException;
import ru.practicum.shareit.exceptions.NotBookerException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private UserRepository userRepository;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(userId, itemDto)), Collections.EMPTY_LIST);
    }

    @Override
    public ItemDto editItem(Long userId, Long itemId, ItemDto itemDto) {
        final var oldItem = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь c id = %s не найдена", itemId)));
        if (!(oldItem.getOwnerId().equals(userId))) {
            throw new NotAccessException("Редактировать данные может только владелец вещи");
        }
        final var item = ItemMapper.toItem(userId, itemDto);
        if (item.getName() != null && !item.getName().isBlank()) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getIsAvailable() != null) {
            oldItem.setIsAvailable(item.getIsAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(oldItem), commentRepository.findAllByItem(oldItem).stream()
                .map(comment -> CommentMapper.toCommentDtoResponse(comment, comment.getAuthor().getName()))
                .collect(Collectors.toList()));
    }

    @Override
    public ItemDto showItemInfo(Long itemId, Long userId) {
        var item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с id = %s не найдена"));
        if (item.getOwnerId().equals(userId)) {
            return addBookings(ItemMapper.toItemDto(item, commentRepository.findAllByItem(item).stream()
                    .map(comment -> CommentMapper.toCommentDtoResponse(comment, comment.getAuthor().getName()))
                    .collect(Collectors.toList())), userId);
        } else {
            return ItemMapper.toItemDto(item, commentRepository.findAllByItem(item).stream()
                    .map(comment -> CommentMapper.toCommentDtoResponse(comment, comment.getAuthor().getName()))
                    .collect(Collectors.toList()));
        }
    }

    @Override
    public List<ItemDto> findAllMyItems(Long userId) {
        return itemRepository.findItemsByOwnerIdOrderById(userId).stream().
                map(item -> ItemMapper.toItemDto(item, commentRepository.findAllByItem(item).stream()
                        .map(comment -> CommentMapper.toCommentDtoResponse(comment, comment.getAuthor().getName()))
                        .collect(Collectors.toList()))).map(itemDto -> addBookings(itemDto, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text)
                .stream().map(item -> ItemMapper.toItemDto(item, commentRepository.findAllByItem(item).stream()
                        .map(comment -> CommentMapper.toCommentDtoResponse(comment, comment.getAuthor().getName()))
                        .collect(Collectors.toList()))).collect(Collectors.toList());
    }

    @Override
    public CommentDtoResponse postComment(Long itemId, Long userId, CommentDtoRequest commentDtoRequest) {
        var item = itemRepository.findById(itemId).orElseThrow(()
                -> new NotFoundException("Вещь не найдена"));
        var user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь на найден"));
        var bookings = bookingRepository.findAllByBookerIdAndItemIdAndEndDateBeforeAndStatus(userId,
                itemId, commentDtoRequest.getCreated(), BookingStatus.APPROVED);
        if (bookings.isEmpty()) {
            throw new NotBookerException(String.format("Пользователь с id = %s не брал в аренду вещь с id = %s",
                    userId, itemId));
        }
        var comment = commentRepository.save(CommentMapper.toComment(commentDtoRequest, user, item));
        return CommentMapper.toCommentDtoResponse(
                comment, userRepository.findById(comment.getAuthor().getId()).get().getName());
    }

    private ItemDto addBookings(ItemDto itemDto, Long userId) {
        var lastBooking = bookingRepository.
                findFirstBookingByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc(itemDto.getId(),
                        LocalDateTime.now(), BookingStatus.REJECTED);
        lastBooking.ifPresent(booking -> itemDto.setLastBooking(BookingMapper.toBookingItemDto(lastBooking.get())));
        var nextBooking = bookingRepository.
                findFirstBookingByItemIdAndStartDateAfterAndStatusNotOrderByStartDate(itemDto.getId(),
                        LocalDateTime.now(), BookingStatus.REJECTED);
        nextBooking.ifPresent(booking -> itemDto.setNextBooking(BookingMapper.toBookingItemDto(nextBooking.get())));
        return itemDto;
    }
}
