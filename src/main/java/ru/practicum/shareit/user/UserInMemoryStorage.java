package ru.practicum.shareit.user;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@Component
public class UserInMemoryStorage implements UserStorage {
    private final HashMap<Long, User> users;
    private Long id = 0L;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        user.setId(userGenerateId());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User updateUser(Long id, User user) {
        if (users.containsKey(id)) {
            final var oldUser = users.get(id);
            if (user.getName() != null) {
                oldUser.setName(user.getName());
            }
            if (user.getEmail() != null) {
                oldUser.setEmail(user.getEmail());
            }
            return oldUser;
        } else {
            throw new NotFoundException("Пользователь с таким ID не найден");
        }
    }

    @Override
    public void deleteUser(Long id) {
        if (users.containsKey(id)) {
            users.remove(id);
        } else {
            throw new NotFoundException("Пользователь с таким ID не найден");
        }
    }

    @Override
    public User getUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException(String.format("Пользователь с Id = %s не найден", userId));
        }
        return users.get(userId);
    }

    private long userGenerateId() {
        return ++id;
    }
}
