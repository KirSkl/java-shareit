package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.common.Constants;
import ru.practicum.shareit.common.Validator;
import ru.practicum.shareit.exceptions.InvalidPageParamsException;
import ru.practicum.shareit.exceptions.ValidationIdException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingService service;
    @MockBean
    private Validator validator;

    private Long userId;
    private BookingDtoRequest bookingDtoRequest;
    private BookingDtoResponse bookingDtoResponse;
    private User user;
    private Item item;
    private Long bookingId;
    private Boolean approved;
    private String state;
    private int from;
    private int size;

    @BeforeEach
    void loadInitial() {
        userId = 1L;
        bookingDtoRequest = new BookingDtoRequest(2L,
                LocalDateTime.of(2040, 4, 27, 12, 1, 1),
                LocalDateTime.of(2040, 4, 27, 13, 1, 1));
        user = new User(1L, "John", "John@mail.com");
        item = new Item(2L, "Hammer", "Very big", true, 1L, 1L);
        bookingId = 3L;
        bookingDtoResponse = new BookingDtoResponse(bookingId, bookingDtoRequest.getStart(),
                bookingDtoRequest.getEnd(), BookingStatus.WAITING, user, item);
        approved = true;
        state = "ALL";
        from = 0;
        size = 10;
    }

    @SneakyThrows
    @Test
    void testCreateBookingOk() {
        when(service.createBooking(userId, bookingDtoRequest)).thenReturn(bookingDtoResponse);

        var result = mvc.perform(post("/bookings")
                        .header(Constants.USER_HEADER, userId)
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingDtoResponse), result);
    }

    @SneakyThrows
    @Test
    void testCreateBookingIdIsNullThenReturnBadRequest() {
        BookingDtoRequest bookingDtoRequestIdNull = new BookingDtoRequest(null, bookingDtoRequest.getStart(),
                bookingDtoRequest.getEnd());

        mvc.perform(post("/bookings")
                        .header(Constants.USER_HEADER, userId)
                        .content(mapper.writeValueAsString(bookingDtoRequestIdNull))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(validator, never()).validateId(anyLong());
        verify(validator, never()).validateBookingDto(any());
        verify(service, never()).createBooking(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void testApprovedBookingOk() {
        when(service.approvedBooking(bookingId, approved, userId)).thenReturn(bookingDtoResponse);

        var result = mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(Constants.USER_HEADER, userId)
                        .param("approved", String.valueOf(approved))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingDtoResponse), result);
    }

    @SneakyThrows
    @Test
    void testApprovedBookingInvalidUserIdThenReturnBadRequest() {
        doThrow(new ValidationIdException("Неверный Id")).when(validator).validateId(userId);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(Constants.USER_HEADER, userId)
                        .param("approved", String.valueOf(approved))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(validator, times(1)).validateId(anyLong());
        verify(service, never()).approvedBooking(anyLong(), any(), anyLong());
    }

    @SneakyThrows
    @Test
    void testGetBookingOk() {
        when(service.getBooking(userId, bookingId)).thenReturn(bookingDtoResponse);

        var result = mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(Constants.USER_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingDtoResponse), result);
    }

    @SneakyThrows
    @Test
    void testGetBookingInvalidBookingIdThenReturnBadRequest() {
        doThrow(new ValidationIdException("Неверный Id")).when(validator).validateId(bookingId);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(Constants.USER_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(validator, times(2)).validateId(anyLong());
        verify(service, never()).getBooking(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void testGetAllBookingsOk() {
        when(service.getAllBookings(userId, state, from, size)).thenReturn(List.of(bookingDtoResponse));

        var result = mvc.perform(get("/bookings")
                        .header(Constants.USER_HEADER, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(bookingDtoResponse)), result);
    }

    @SneakyThrows
    @Test
    void testGetAllBookingsWithoutParamsOk() {
        when(service.getAllBookings(userId, state, from, size)).thenReturn(List.of(bookingDtoResponse));

        var result = mvc.perform(get("/bookings")
                        .header(Constants.USER_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(bookingDtoResponse)), result);
    }

    @SneakyThrows
    @Test
    void testGetAllItemBookingsOk() {
        when(service.getAllItemBookings(userId, state, from, size)).thenReturn(List.of(bookingDtoResponse));

        var result = mvc.perform(get("/bookings/owner")
                        .header(Constants.USER_HEADER, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(bookingDtoResponse)), result);
    }

    @SneakyThrows
    @Test
    void testGetAllItemBookingsInvalidParamsThenReturnBadRequest() {
        doThrow(new InvalidPageParamsException("Неверные параметры пагинации")).when(validator)
                .validatePageParams(from, size);

        mvc.perform(get("/bookings/owner")
                        .header(Constants.USER_HEADER, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(validator, times(1)).validateId(userId);
        verify(validator, times(1)).validatePageParams(from, size);
        verify(service, never()).getAllItemBookings(anyLong(), any(), anyInt(), anyInt());
    }
}
