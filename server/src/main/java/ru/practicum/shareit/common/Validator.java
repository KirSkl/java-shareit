package ru.practicum.shareit.common;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserRepository;

@Component
@Slf4j
@AllArgsConstructor
public class Validator {
    @Autowired
    private UserRepository userRepository;

    public void checkIsUserExists(Long id) {
        log.info("Проверка наличия пользователя...");
        userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}
