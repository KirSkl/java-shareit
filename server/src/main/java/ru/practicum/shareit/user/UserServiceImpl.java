package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        final var oldUser = findUser(id);
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            oldUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            oldUser.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(oldUser));
    }

    @Override
    public void deleteUser(Long id) {
        if (userRepository.deleteByIdAndReturnCount(id) != 1) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    public UserDto getUser(Long userId) {
        return UserMapper.toUserDto(findUser(userId));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь c id = %s не найден", userId)));
    }
}
