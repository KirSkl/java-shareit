package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Validator;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private UserService userService;
    private Validator validator;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Получен запрос GET/users на получение списка пользователей");
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info(String.format("Получен запрос GET/users/userId=%s - получение пользователя по ID", userId));
        validator.validateId(userId);
        return userService.getUser(userId);
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody User user) {
        log.info("Получен запрос \"POST /users\" на создание пользователя");
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody User user) {
        log.info(String.format("Получен запрос PATCH/users/userId=%s на обновление данных пользователя", userId));
        validator.validateId(userId);
        return userService.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info(String.format("Получен запрос \"DELETE /users/userId=%s\" на удаление пользователя", userId));
        validator.validateId(userId);
        userService.deleteUser(userId);
    }

}
