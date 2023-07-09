package ru.practicum.shareit.common;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EmailAlreadyIsUsed;
import ru.practicum.shareit.exceptions.ValidationIdException;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
@AllArgsConstructor
public class Validator {
    @Autowired
    private UserStorage userStorage;

    public void validateDuplicateEmailWhenUpdate(Long id, User user) {
        if (user.getEmail() != null) {
            log.info("Проверка email...");
            final var users = userStorage.getAll().stream()
                    .filter(u -> !Objects.equals(u.getId(), id))
                    .collect(Collectors.toList());
            if (users.isEmpty()) {
                return;
            }
            validateDuplicateEmail(users, user);
        }
    }

    public void validateDuplicateEmailWhenCreate(User user) {
        if (user.getEmail() != null) {
            log.info("Проверка email...");
            final var users = userStorage.getAll();
            if (users.isEmpty()) {
                return;
            }
            validateDuplicateEmail(users, user);
        }
    }

    public void validateId(Long id) {
        log.info("Проверка id...");
        if (id < 1) {
            log.info("Ошибочный запрос - такой ID не может существовать");
            throw new ValidationIdException("ID меньше 1");
        }
    }

    public void checkIsUserExists(Long id) {
        log.info("Проверка наличия пользователя...");
        userStorage.getUser(id);
    }

    private void validateDuplicateEmail(List<User> users, User user) {
        if (users.stream().anyMatch(
                u -> u.getEmail().equals(user.getEmail()))) {
            log.info("Указан уже используемый email");
            throw new EmailAlreadyIsUsed(
                    String.format("Данный адрес %s уже используется", user.getEmail()));
        }
    }
}
