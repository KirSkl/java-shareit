package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.ItemRepository;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    BookingRepository bookingRepository;
    ItemRepository itemRepository;
    @Override
    public BookingDto createBooking(Long userId, BookingDto bookingDto) {
        var ownerId = itemRepository.findById(bookingDto.getItemId()).get().getOwnerId();
        return BookingMapper.toBookingDto(bookingRepository.save(
                BookingMapper.toBooking(bookingDto,userId, ownerId)));
    }
}
