package ru.practicum.shareit.booking.model;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;
@Data
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date", nullable = false)
    private Instant startDate;
    @Column(name = "end_date", nullable = false)
    private Instant endDate;
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    @Column(name = "booker_id", nullable = false)
    private Long bookerId;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
