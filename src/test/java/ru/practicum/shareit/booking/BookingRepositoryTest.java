package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    BookingRepository repository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    Booking bookingPastItem;
    Booking bookingCurrentItem2;
    Booking bookingFutureItem2;
    Booking bookingCurrentNextItem;
    Item item;
    Item item2;
    User user;
    User user2;
    Pageable page;

    @BeforeEach
    void loadInitial() {
        user = userRepository.save(new User(null, "John", "john@doe.com"));
        item = itemRepository.save(new Item(null, "Hammer", "Very big", true,
                user.getId(), null));
        user2 = userRepository.save(new User(null, "Jack", "jack@doe.com"));
        item2 = itemRepository.save(new Item(null, "Iron", "Very hot", false,
                user2.getId(), null));
        bookingPastItem = repository.save(new Booking(null,
                LocalDateTime.of(2000, 1, 1, 1, 1, 1),
                LocalDateTime.of(2000, 1, 1, 2, 1, 1),
                item, user2, BookingStatus.WAITING));
        bookingCurrentItem2 = repository.save(new Booking(null,
                LocalDateTime.of(2022, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                item2, user, BookingStatus.APPROVED));
        bookingCurrentNextItem = repository.save(new Booking(null,
                LocalDateTime.of(2022, 2, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                item, user2, BookingStatus.REJECTED));
        bookingFutureItem2 = repository.save(new Booking(null,
                LocalDateTime.of(2025, 1, 1, 1, 1, 1),
                LocalDateTime.of(2025, 1, 1, 2, 1, 1),
                item2, user, BookingStatus.APPROVED));
        page = PageRequest.of(0, 1);
    }

    @Test
    void testFindAllByBookerIdOrderByStartDateDesc() {
        var result = repository.findAllByBookerIdOrderByStartDateDesc(user.getId(), page);

        assertTrue(result.size() == 1);
        assertEquals(bookingFutureItem2, result.get(0));
    }

    @Test
    void testFindAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc() {
        var result = repository.findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                user2.getId(), LocalDateTime.now(), LocalDateTime.now(), page);

        assertTrue(result.size() == 1);
        assertEquals(bookingCurrentNextItem, result.get(0));
    }

    @Test
    void testFindAllByBookerIdAndEndDateBeforeOrderByStartDateDesc() {
        var result = repository.findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(
                user2.getId(), LocalDateTime.now(), page);

        assertTrue(result.size() == 1);
        assertEquals(bookingPastItem, result.get(0));
    }

    @Test
    void testFindAllByBookerIdAndStartDateAfterOrderByStartDateDesc() {
        var result = repository.findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(
                user.getId(), LocalDateTime.now(), page);

        assertTrue(result.size() == 1);
        assertEquals(bookingFutureItem2, result.get(0));
    }

    @Test
    void testFindAllByBookerIdAndStatusOrderByStartDateDesc() {
        var resultApproved = repository.findAllByBookerIdAndStatusOrderByStartDateDesc(
                user.getId(), BookingStatus.APPROVED, page);
        var resultRejected = repository.findAllByBookerIdAndStatusOrderByStartDateDesc(
                user.getId(), BookingStatus.REJECTED, page);
        var resultWaiting = repository.findAllByBookerIdAndStatusOrderByStartDateDesc(
                user2.getId(), BookingStatus.WAITING, page);

        assertTrue(resultApproved.size() == 1);
        assertEquals(bookingFutureItem2, resultApproved.get(0));

        assertEquals(Collections.emptyList(), resultRejected);

        assertTrue(resultWaiting.size() == 1);
        assertEquals(bookingPastItem, resultWaiting.get(0));
    }

    @Test
    void testFindAllByItemOwnerIdOrderByStartDateDesc() {
        var result = repository.findAllByItemOwnerIdOrderByStartDateDesc(item.getOwnerId(), page);

        assertTrue(result.size() == 1);
        assertEquals(bookingCurrentNextItem, result.get(0));
    }

    @Test
    void testFindAllByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc() {
        var result = repository.findAllByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                item2.getOwnerId(), LocalDateTime.now(), LocalDateTime.now(), page);

        assertTrue(result.size() == 1);
        assertEquals(bookingCurrentItem2, result.get(0));
    }

    @Test
    void testFindAllByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc() {
        var result = repository.findAllByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(
                user.getId(), LocalDateTime.now(), page);

        assertTrue(result.size() == 1);
        assertEquals(bookingPastItem, result.get(0));
    }

    @Test
    void testFindAllByItemOwnerIdAndStartDateAfterOrderByStartDateDesc() {
        var result = repository.findAllByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(
                user2.getId(), LocalDateTime.now(), page);

        assertTrue(result.size() == 1);
        assertEquals(bookingFutureItem2, result.get(0));
    }

    @Test
    void testFindAllByItemOwnerIdAndStatusOrderByStartDateDesc() {
        var resultApproved = repository.findAllByItemOwnerIdAndStatusOrderByStartDateDesc(
                user2.getId(), BookingStatus.APPROVED, page);
        var resultRejected = repository.findAllByItemOwnerIdAndStatusOrderByStartDateDesc(
                user2.getId(), BookingStatus.REJECTED, page);
        var resultWaiting = repository.findAllByItemOwnerIdAndStatusOrderByStartDateDesc(
                user.getId(), BookingStatus.WAITING, page);

        assertTrue(resultApproved.size() == 1);
        assertEquals(bookingFutureItem2, resultApproved.get(0));

        assertEquals(Collections.emptyList(), resultRejected);

        assertTrue(resultWaiting.size() == 1);
        assertEquals(bookingPastItem, resultWaiting.get(0));
    }

    @Test
    void testFindAllByBookerIdAndItemIdAndEndDateBeforeAndStatus() {
        var result = repository.findAllByBookerIdAndItemIdAndEndDateBeforeAndStatus(
                user2.getId(), item.getId(), LocalDateTime.now(), BookingStatus.APPROVED);

        assertTrue(result.size() == 0);
    }

    @Test
    void testFindFirstBookingByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc() {
        var result = repository
                .findFirstBookingByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc(
                        item.getId(), LocalDateTime.now(), BookingStatus.REJECTED);

        assertTrue(result.isPresent());
        assertEquals(bookingPastItem, result.get());
    }

    @Test
    void testFindFirstBookingByItemIdAndStartDateAfterAndStatusNotOrderByStartDate() {
        var result = repository.findFirstBookingByItemIdAndStartDateAfterAndStatusNotOrderByStartDate(
                item2.getId(), LocalDateTime.now(), BookingStatus.REJECTED);

        assertTrue(result.isPresent());
        assertEquals(bookingFutureItem2, result.get());
    }

}
