package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStates;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDateDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
            Long bookerId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(Long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(Long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDateDesc(Long bookerId, BookingStatus state);

    List<Booking> findAllByItemOwnerIdOrderByStartDateDesc(Long bookerId);

    List<Booking> findAllByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
            Long bookerId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findAllByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(Long bookerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(Long bookerId, LocalDateTime now);

    Optional<Booking> findFirstBookingByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc(Long itemId,
                      LocalDateTime now, BookingStatus bookingStatus);

    Optional<Booking> findFirstBookingByItemIdAndStartDateAfterAndStatusNotOrderByStartDate(Long itemId,
                     LocalDateTime now, BookingStatus bookingStatus);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDateDesc(Long userId, BookingStatus valueOf);


    List<Booking> findAllByBookerIdAndItemIdAndEndDateBeforeAndStatus(Long bookerId, Long itemId, LocalDateTime created,
                                                                      BookingStatus bookingStatus);
}
