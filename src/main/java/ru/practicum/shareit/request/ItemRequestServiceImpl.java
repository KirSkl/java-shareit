package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponseWithAnswers;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService{
    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @Override
    public ItemRequestDtoResponse addRequest(Long userId, ItemRequestDto itemRequestDto) {
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id = %s не найден", userId)));
        return ItemRequestMapper.toItemRequestDtoResponse(itemRequestRepository.save(
                ItemRequestMapper.toItemRequest(user, itemRequestDto)));
    }

    @Override
    public List<ItemRequestDtoResponseWithAnswers> getMyRequests(Long userId) {
        return itemRequestRepository.findAllByUserIdOrderByCreatedDesc(userId).stream().map(itemRequest ->
                ItemRequestMapper.toItemRequestDtoResponseWithAnswers(itemRequest, itemRepository.findAllByRequestId(
                        itemRequest.getId()).stream().map(ItemMapper::toItemRequestAnswerDto)
                        .collect(Collectors.toList()))).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoResponseWithAnswers findItemRequest(Long requestId) {
        var itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Запрос с id = %s не найден", requestId)));
        return ItemRequestMapper.toItemRequestDtoResponseWithAnswers(itemRequest, itemRepository.findAllByRequestId(
                        itemRequest.getId()).stream().map(ItemMapper::toItemRequestAnswerDto)
                        .collect(Collectors.toList()));
    }
}
