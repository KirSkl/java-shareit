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

    private Booking bookingPast;
    private Booking bookingCurrent;
    private Booking bookingFuture;
    private Item item;
    private User user;
    private Pageable page;

    @BeforeEach
    void loadInitial() {
        user = userRepository.save(new User(null, "John", "john@doe.com"));
        item = itemRepository.save(new Item(null, "Hammer", "Very big", true,
                user.getId(), null));
        bookingPast = repository.save(new Booking(null,
                LocalDateTime.of(2000, 1, 1, 1, 1, 1),
                LocalDateTime.of(2000, 1, 1, 2, 1, 1),
                item, user, BookingStatus.APPROVED));
        bookingCurrent =  repository.save(new Booking(null,
                LocalDateTime.of(2022, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                item, user, BookingStatus.APPROVED));
        bookingFuture = repository.save(new Booking(null,
                LocalDateTime.of(2025, 1, 1, 1, 1, 1),
                LocalDateTime.of(2025, 1, 1, 2, 1, 1),
                item, user, BookingStatus.APPROVED));
        page = PageRequest.of(0, 2);
    }

    @Test
    void testFindAllByBookerIdOrderByStartDateDesc() {
        var result = repository.findAllByBookerIdOrderByStartDateDesc(user.getId(), page);

        assertTrue(result.size() == 2);
        assertEquals(bookingFuture, result.get(0));
        assertEquals(bookingCurrent, result.get(1));
    }
}
