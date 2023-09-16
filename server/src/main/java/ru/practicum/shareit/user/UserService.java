package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long id, UserDto user);

    void deleteUser(Long id);

    UserDto getUser(Long userId);
}
