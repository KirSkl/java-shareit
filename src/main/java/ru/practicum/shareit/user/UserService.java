package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto createUser(User user);

    UserDto updateUser(Long id, User user);

    void deleteUser(Long id);

    UserDto getUser(Long userId);
}
