package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    ItemRequestRepository repository;
    @Autowired
    UserRepository userRepository;

    private User user;
    private User userAnother;
    private ItemRequest itemRequest;
    private ItemRequest itemRequest2;

    @BeforeEach
    void loadInitial() {
        user = userRepository.save(new User(null, "John", "john@doe.com"));
        userAnother = userRepository.save(new User(null, "Adam", "adam@smith.com"));

        itemRequest = repository.save(new ItemRequest(null, "Need hammer", user,
                LocalDateTime.of(2000, 1, 1, 1, 1, 1)));
        itemRequest2 = repository.save(new ItemRequest(null, "Need gamepad", user,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1)));
        ItemRequest itemRequestAnotherUser = repository.save(new ItemRequest(null, "Need battery", userAnother,
                LocalDateTime.of(2001, 1, 1, 1, 1, 1)));

    }

    @Test
    void testFindAllByUserIdOrderByCreatedDescOk() {
        var result = repository.findAllByUserIdOrderByCreatedDesc(user.getId());

        assertEquals(2, result.size());
        assertEquals(itemRequest2.getId(), result.get(0).getId());
        assertEquals(itemRequest2.getDescription(), result.get(0).getDescription());
        assertEquals(itemRequest2.getCreated(), result.get(0).getCreated());
        assertEquals(itemRequest.getId(), result.get(1).getId());
        assertEquals(itemRequest.getDescription(), result.get(1).getDescription());
    }

    @Test
    void testFindAllByUserIdNotOrderByCreatedDesc() {
        var result = repository.findAllByUserIdNotOrderByCreatedDesc(PageRequest.of(0, 1),
                userAnother.getId());

        assertEquals(1, result.size());
        assertEquals(itemRequest2.getId(), result.get(0).getId());
        assertEquals(itemRequest2.getDescription(), result.get(0).getDescription());
        assertEquals(itemRequest2.getUser(), result.get(0).getUser());
        assertEquals(itemRequest2.getCreated(), result.get(0).getCreated());
    }
}
