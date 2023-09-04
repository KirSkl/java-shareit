package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDateDesc(Long bookerId, Pageable page);

    List<Booking> findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
            Long bookerId, LocalDateTime now, LocalDateTime now1, Pageable page);

    List<Booking> findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(Long bookerId, LocalDateTime now, Pageable page);

    List<Booking> findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(Long bookerId, LocalDateTime now, Pageable page);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDateDesc(Long bookerId, BookingStatus state, Pageable page);

    List<Booking> findAllByItemOwnerIdOrderByStartDateDesc(Long ownerId, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
            Long ownerId, LocalDateTime now, LocalDateTime now1, Pageable page);

    List<Booking> findAllByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(Long ownerId, LocalDateTime now, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(Long bookerId, LocalDateTime now, Pageable page);

    Optional<Booking> findFirstBookingByItemIdAndStartDateBeforeAndStatusNotOrderByStartDateDesc(
            Long itemId, LocalDateTime now, BookingStatus bookingStatus);

    Optional<Booking> findFirstBookingByItemIdAndStartDateAfterAndStatusNotOrderByStartDate(
            Long itemId, LocalDateTime now, BookingStatus bookingStatus);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDateDesc(Long userId, BookingStatus valueOf, Pageable page);


    List<Booking> findAllByBookerIdAndItemIdAndEndDateBeforeAndStatus(Long bookerId, Long itemId, LocalDateTime created,
                                                                      BookingStatus bookingStatus);
}
