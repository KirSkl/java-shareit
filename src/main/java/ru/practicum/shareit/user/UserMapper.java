package ru.practicum.shareit.user;

import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

@Data
public class UserMapper {

    private static UserDto toUserDto(User user) {
        return new UserDto (
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
