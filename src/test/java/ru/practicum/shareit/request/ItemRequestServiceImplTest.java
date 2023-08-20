package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponseWithAnswers;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    private User user;
    private ItemRequest request;
    private ItemRequestDto requestDto;
    private ItemRequestDtoResponse requestDtoResponse;
    private ItemRequestDtoResponseWithAnswers requestDtoResponseWithAnswers;

    @BeforeEach
    void loadInitial() {
        user = new User(1L, "John Doe", "John_Doe@mail.com");
        request = new ItemRequest(1L, "Need hammer", user, LocalDateTime.now());
        requestDto = new ItemRequestDto(request.getDescription());
        requestDtoResponse = new ItemRequestDtoResponse(request.getId(), request.getDescription(),
                request.getCreated());
        requestDtoResponseWithAnswers = new ItemRequestDtoResponseWithAnswers(request.getId(), request.getDescription(),
                request.getCreated(), Collections.emptyList());
    }

    @Test
    void addRequestOk() {
        var req = ItemRequestMapper.toItemRequest(user, requestDto);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.save(req)).thenReturn(request);

        var result = itemRequestService.addRequest(user.getId(), requestDto);

        assertEquals(requestDtoResponse, result);
        verify(requestRepository, times(1)).save(req);
    }

    @Test
    void addRequestThrownNotFound() {
        when(userRepository.findById(anyLong())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.addRequest(user.getId(), requestDto));
        verify(requestRepository, never()).save(any());
    }

    @Test
    void testGetMyRequestsOk() {
        when(requestRepository.findAllByUserIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(Collections.emptyList());

        var result = itemRequestService.getMyRequests(user.getId());

        assertEquals(List.of(requestDtoResponseWithAnswers), result);
        verify(requestRepository, times(1)).findAllByUserIdOrderByCreatedDesc(any());
    }

    @Test
    void testGetMyRequestsIfEmptyOk() {
        when(requestRepository.findAllByUserIdOrderByCreatedDesc(anyLong())).thenReturn(Collections.emptyList());

        var result = itemRequestService.getMyRequests(user.getId());

        assertEquals(Collections.emptyList(), result);
        verify(requestRepository, times(1)).findAllByUserIdOrderByCreatedDesc(anyLong());
    }

    @Test
    void testFindItemRequestOk() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(Collections.emptyList());

        var result = itemRequestService.findItemRequest(request.getId());

        assertEquals(requestDtoResponseWithAnswers, result);
        verify(requestRepository, times(1)).findById(anyLong());
    }

    @Test
    void testFindItemRequestThrownNotFound() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.findItemRequest(anyLong()));
    }

    @Test
    void testGetAllOk() {
        when(requestRepository.findAllByUserIdNotOrderByCreatedDesc(any(), anyLong())).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(Collections.emptyList());
        final int from = 0;
        final int size = 10;

        var result = itemRequestService.getAll(from, size, user.getId());

        assertEquals(List.of(requestDtoResponseWithAnswers), result);
        verify(requestRepository, times(1)).findAllByUserIdNotOrderByCreatedDesc(any(), any());
    }

    @Test
    void testGetAllIfEmptyOk() {
        when(requestRepository.findAllByUserIdNotOrderByCreatedDesc(any(), anyLong())).thenReturn(
                Collections.emptyList());
        final int from = 0;
        final int size = 10;

        var result = itemRequestService.getAll(from, size, user.getId());
        assertEquals(Collections.emptyList(), result);
        verify(requestRepository, times(1))
                .findAllByUserIdNotOrderByCreatedDesc(any(), anyLong());
    }
}
