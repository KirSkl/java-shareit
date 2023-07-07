package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAll();

    User createUser(User user);

    User updateUser(Long id, User user);

    void deleteUser(Long id);

    User getUser(Long userId);
}
