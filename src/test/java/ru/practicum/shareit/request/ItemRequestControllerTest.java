package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.common.Constants;
import ru.practicum.shareit.common.Validator;
import ru.practicum.shareit.exceptions.InvalidPageParamsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationIdException;
import ru.practicum.shareit.request.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponseWithAnswers;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    ItemRequestService service;
    @MockBean
    Validator validator;

    Long userId;
    ItemRequestDto itemRequestDto;
    ItemRequestDtoResponse itemRequestDtoResponse;
    ItemRequestDtoResponseWithAnswers itemRequestDtoResponseWithAnswers;
    ItemRequestAnswerDto itemRequestAnswerDto;
    Long requestId;
    int from;
    int size;

    @BeforeEach
    void loadInitial() {
        userId = 1L;
        itemRequestDto = new ItemRequestDto("Нужен молоток");
        itemRequestDtoResponse = new ItemRequestDtoResponse(1L, "Нужен молоток", LocalDateTime.now());
        itemRequestAnswerDto = new ItemRequestAnswerDto(1L, "Hammer", "Heavy",
                true, itemRequestDtoResponse.getId());
        itemRequestDtoResponseWithAnswers = new ItemRequestDtoResponseWithAnswers(itemRequestDtoResponse.getId(),
                itemRequestDtoResponse.getDescription(), itemRequestDtoResponse.getCreated(),
                List.of(itemRequestAnswerDto));
        requestId = 1L;
        from = 0;
        size = 10;
    }

    @SneakyThrows
    @Test
    void testAddRequestOk() {
        when(service.addRequest(userId, itemRequestDto)).thenReturn(itemRequestDtoResponse);

        mvc.perform(post("/requests")
                        .header(Constants.USER_HEADER, userId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoResponse.getDescription())));
    }

    @SneakyThrows
    @Test
    void addRequestInvalidIdThrown() {
        doThrow(new ValidationIdException("Неверный id")).when(validator).validateId(userId);

        mvc.perform(post("/requests")
                        .header(Constants.USER_HEADER, userId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(validator, never()).checkIsUserExists(any());
        verify(service, never()).addRequest(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void testGetMyRequestsOk() {
        when(service.getMyRequests(userId)).thenReturn(List.of(itemRequestDtoResponseWithAnswers));

        mvc.perform(get("/requests")
                        .header(Constants.USER_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoResponseWithAnswers.getId()), Long.class))
                .andExpect(jsonPath("$[0].description",
                        is(itemRequestDtoResponseWithAnswers.getDescription())))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(itemRequestAnswerDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemRequestAnswerDto.getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(itemRequestAnswerDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemRequestAnswerDto.getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(itemRequestAnswerDto.getRequestId()),
                        Long.class));
    }

    @SneakyThrows
    @Test
    void testGetMyRequestsIfUserNotExistsThrownNotFound() {
        doThrow(new NotFoundException("Пользователь не найден")).when(validator).checkIsUserExists(userId);

        mvc.perform(get("/requests")
                        .header(Constants.USER_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(validator, times(1)).validateId(userId);
        verify(validator, times(1)).checkIsUserExists(userId);
        verify(service, never()).getMyRequests(anyLong());
    }

    @SneakyThrows
    @Test
    void testFindItemRequestOk() {
        when(service.findItemRequest(requestId)).thenReturn(itemRequestDtoResponseWithAnswers);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .header(Constants.USER_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoResponseWithAnswers.getId()), Long.class))
                .andExpect(jsonPath("$.description",
                        is(itemRequestDtoResponseWithAnswers.getDescription())))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id", is(itemRequestAnswerDto.getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(itemRequestAnswerDto.getName())))
                .andExpect(jsonPath("$.items[0].description", is(itemRequestAnswerDto.getDescription())))
                .andExpect(jsonPath("$.items[0].available", is(itemRequestAnswerDto.getAvailable())))
                .andExpect(jsonPath("$.items[0].requestId", is(itemRequestAnswerDto.getRequestId()),
                        Long.class));
    }

    @SneakyThrows
    @Test
    void testFindItemRequestIfRequestIdIsNotValidThrown() {
        doThrow(new ValidationIdException("Неверный id")).when(validator).validateId(requestId);

        userId = 2L;

        mvc.perform(get("/requests/{requestId}", requestId)
                        .header(Constants.USER_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(validator, times(1)).validateId(requestId);
        verify(validator, never()).validateId(userId);
        verify(validator, never()).checkIsUserExists(anyLong());
        verify(service, never()).findItemRequest(anyLong());
    }

    @SneakyThrows
    @Test
    void testGetAllRequestsOk() {
        when(service.getAll(anyInt(), anyInt(), anyLong())).thenReturn(List.of(itemRequestDtoResponseWithAnswers));

        mvc.perform(get("/requests/all")
                        .header(Constants.USER_HEADER, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoResponseWithAnswers.getId()), Long.class))
                .andExpect(jsonPath("$[0].description",
                        is(itemRequestDtoResponseWithAnswers.getDescription())))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(itemRequestAnswerDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemRequestAnswerDto.getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(itemRequestAnswerDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemRequestAnswerDto.getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(itemRequestAnswerDto.getRequestId()),
                        Long.class));
    }

    @SneakyThrows
    @Test
    void testGetAllRequestsWithoutParamsOk() {
        when(service.getAll(anyInt(), anyInt(), anyLong())).thenReturn(List.of(itemRequestDtoResponseWithAnswers));

        mvc.perform(get("/requests/all")
                        .header(Constants.USER_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoResponseWithAnswers.getId()), Long.class))
                .andExpect(jsonPath("$[0].description",
                        is(itemRequestDtoResponseWithAnswers.getDescription())))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(itemRequestAnswerDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemRequestAnswerDto.getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(itemRequestAnswerDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemRequestAnswerDto.getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(itemRequestAnswerDto.getRequestId()),
                        Long.class));
    }

    @SneakyThrows
    @Test
    void testGetAllRequestsInvalidParamsThrown() {
        doThrow(new InvalidPageParamsException("Неверные параметры")).when(validator).validatePageParams(from, size);

        mvc.perform(get("/requests/all")
                        .header(Constants.USER_HEADER, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(validator, times(1)).validatePageParams(from, size);
        verify(validator, never()).validateId(anyLong());
        verify(validator, never()).checkIsUserExists(anyLong());
        verify(service, never()).getAll(anyInt(), anyInt(), anyLong());
    }
}
