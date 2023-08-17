package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService{
    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;

    @Override
    public ItemRequestDtoResponse addRequest(Long userId, ItemRequestDto itemRequestDto) {
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id = %s не найден", userId)));
        return ItemRequestMapper.toItemRequestDtoResponse(itemRequestRepository.save(
                ItemRequestMapper.toItemRequest(user, itemRequestDto)));
    }
}
