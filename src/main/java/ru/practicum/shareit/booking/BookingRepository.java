package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStates;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDateDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
            Long bookerId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(Long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(Long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDateDesc(Long bookerId, BookingStates state);

    List<Booking> findAllByItemOwnerIdOrderByStartDateDesc(Long bookerId);

    List<Booking> findAllByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
            Long bookerId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findAllByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(Long bookerId, LocalDateTime now);
    List<Booking> findAllByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(Long bookerId, LocalDateTime now);
    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDateDesc(Long bookerId, BookingStates state);




}
