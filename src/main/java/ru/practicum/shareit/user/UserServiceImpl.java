package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    UserStorage userStorage;

    @Override
    public List<UserDto> getAll() {
        final var usersDto = new ArrayList<UserDto>();
        userStorage.getAll().forEach(u -> usersDto.add(UserMapper.toUserDto(u)));
        return usersDto;
    }

    @Override
    public UserDto createUser(User user) {
        return UserMapper.toUserDto(userStorage.createUser(user));
    }

    @Override
    public UserDto updateUser(Long id, User user) {
        return UserMapper.toUserDto(userStorage.updateUser(id, user));
    }

    @Override
    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    @Override
    public UserDto getUser(Long userId) {
        return UserMapper.toUserDto(userStorage.getUser(userId));
    }
}
