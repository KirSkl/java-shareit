package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.common.Constants;
import ru.practicum.shareit.common.Validator;
import ru.practicum.shareit.exceptions.ValidationIdException;
import ru.practicum.shareit.request.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponseWithAnswers;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemRequestService service;
    @MockBean
    private Validator validator;

    private Long userId;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoResponse itemRequestDtoResponse;
    private ItemRequestDtoResponseWithAnswers itemRequestDtoResponseWithAnswers;
    private ItemRequestAnswerDto itemRequestAnswerDto;

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
}
