package ru.practicum.shareit.item;

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
import ru.practicum.shareit.exceptions.ValidationIdException;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemService service;
    @MockBean
    private Validator validator;

    private ItemDto itemDtoReq;
    private ItemDto itemDtoResp;
    private CommentDtoResponse commentDtoResponse;
    private Long itemId;
    private Long userId;
    private int from;
    private int size;
    private CommentDtoRequest commentDtoRequest;
    private String text;


    @BeforeEach
    void loadInitial() {
        commentDtoRequest = new CommentDtoRequest("Хорошая вещь");
        commentDtoRequest.setCreated(LocalDateTime.of(2000, 4, 27, 12, 1, 1));
        commentDtoResponse = new CommentDtoResponse(1L, "Хорошая вещь", "Иван",
                commentDtoRequest.getCreated());
        itemDtoResp = new ItemDto(1L, "Hammer", "Very big", true, null,
                null, null, List.of(commentDtoResponse));
        itemDtoReq = new ItemDto(null, "Hammer", "Very big", true, null,
                null, null, null);
        userId = 2L;
        itemId = 1L;
        from = 0;
        size = 10;
        text = "Молоток";
    }

    @SneakyThrows
    @Test
    void testAddItemOk() {
        when(service.addItem(userId, itemDtoReq)).thenReturn(itemDtoResp);

        mvc.perform(post("/items")
                        .header(Constants.USER_HEADER, userId)
                        .content(mapper.writeValueAsString(itemDtoReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoResp.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoResp.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoResp.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoResp.getAvailable())));
    }

    @SneakyThrows
    @Test
    void testAddItemInvalidIdThrown() {
        doThrow(new ValidationIdException("Неверный id")).when(validator).validateId(userId);

        mvc.perform(post("/items")
                        .header(Constants.USER_HEADER, userId)
                        .content(mapper.writeValueAsString(itemDtoReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(validator, never()).checkIsUserExists(anyLong());
        verify(service, never()).addItem(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void testAddItemNameIsBlankThrown() {
        itemDtoReq.setName("");

        mvc.perform(post("/items")
                        .header(Constants.USER_HEADER, userId)
                        .content(mapper.writeValueAsString(itemDtoReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, never()).addItem(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void testEditItemOk() {
        when(service.editItem(userId, itemId, itemDtoReq)).thenReturn(itemDtoResp);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header(Constants.USER_HEADER, userId)
                        .content(mapper.writeValueAsString(itemDtoReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoResp.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoResp.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoResp.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoResp.getAvailable())));
    }

    @SneakyThrows
    @Test
    void testEditItemInvalidItemIdThrown() {
        doThrow(new ValidationIdException("Неверный id")).when(validator).validateId(itemId);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header(Constants.USER_HEADER, userId)
                        .content(mapper.writeValueAsString(itemDtoReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(validator, times(1)).validateId(userId);
        verify(validator, times(1)).validateId(itemId);
        verify(service, never()).editItem(anyLong(), anyLong(), any());
    }

    @SneakyThrows
    @Test
    void testShowItemInfoOk() {
        when(service.showItemInfo(itemId, userId)).thenReturn(itemDtoResp);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(Constants.USER_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoResp.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoResp.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoResp.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoResp.getAvailable())));
    }

    @SneakyThrows
    @Test
    void testFindAllMyItemsOk() {
        when(service.findAllMyItems(userId, from, size)).thenReturn(List.of(itemDtoResp));

        mvc.perform(get("/items")
                        .header(Constants.USER_HEADER, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDtoResp.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoResp.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoResp.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoResp.getAvailable())));
    }

    @SneakyThrows
    @Test
    void testFindAllMyItemsWithoutParamsOk() {
        when(service.findAllMyItems(userId, from, size)).thenReturn(List.of(itemDtoResp));

        mvc.perform(get("/items")
                        .header(Constants.USER_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDtoResp.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoResp.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoResp.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoResp.getAvailable())));
    }

    @SneakyThrows
    @Test
    void testSearchOk() {
        when(service.search(text, from, size)).thenReturn(List.of(itemDtoResp));

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDtoResp.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoResp.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoResp.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoResp.getAvailable())));
    }

    @SneakyThrows
    @Test
    void testSearchInvalidParamsThrown() {
        doThrow(new InvalidPageParamsException("Неверные параметры пагинации"))
                .when(validator).validatePageParams(from, size);

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, never()).search(any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void testPostCommentOk() {
        when(service.postComment(itemId, userId, commentDtoRequest)).thenReturn(commentDtoResponse);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(Constants.USER_HEADER, userId)
                        .content(mapper.writeValueAsString(commentDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDtoResponse))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoResponse.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoResponse.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDtoResponse.getCreated().toString())));
    }

    @SneakyThrows
    @Test
    void testPostCommentTextIsBlankThrown() {
        commentDtoRequest.setText("");

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(Constants.USER_HEADER, userId)
                        .content(mapper.writeValueAsString(commentDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(validator, never()).validateId(anyLong());
        verify(service, never()).postComment(anyLong(), anyLong(), any());
    }
}
