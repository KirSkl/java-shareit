package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    ItemRepository repository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    private Item item;
    private Item itemAnother;
    private Item itemAnotherOwner;
    private Pageable page;
    private User user;
    private User userAnother;
    private ItemRequest itemRequest;
    private ItemRequest itemRequestAnother;

    @BeforeEach
    void loadInitial() {
        user = userRepository.save(new User(null, "John", "john@doe.com"));
        userAnother = userRepository.save(new User(null, "Adam", "adam@smith.com"));

        itemRequest = itemRequestRepository.save(new ItemRequest(null, "Need hammer", userAnother,
                LocalDateTime.now()));
        itemRequestAnother = itemRequestRepository.save(new ItemRequest(null, "Need hood", userAnother,
                LocalDateTime.now()));

        item = repository.save(new Item(null, "Hammer", "Very big", true, user.getId(),
                itemRequest.getId()));
        itemAnother = repository.save(new Item(null, "Hood", "Black", true, user.getId(),
                itemRequestAnother.getId()));
        itemAnotherOwner = repository.save(new Item(null, "Bicycle", "Very fast", false,
                userAnother.getId(), null));

        page = PageRequest.of(0, 1);
    }

    @AfterEach
    void clearData() {
        repository.deleteAll(); //
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testFindItemsByOwnerIdOrderByIdShouldReturnOneItem() {
        var result = repository.findItemsByOwnerIdOrderById(user.getId(), page);

        assertTrue(result.size() == 1);
        assertEquals(item.getId(), result.get(0).getId());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getDescription(), result.get(0).getDescription());
    }

    @Test
    void testFindAllByRequestIdShouldReturnOneItem() {
        var result = repository.findAllByRequestId(itemRequest.getId());

        assertTrue(result.size() == 1);
        assertEquals(item.getId(), result.get(0).getId());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getDescription(), result.get(0).getDescription());
    }

    @Test
    void testSearchOk() {
        page = PageRequest.of(0, 10);
        var result = repository.search("Hamm", page);

        assertTrue(result.size() == 1);
        assertEquals(item.getId(), result.get(0).getId());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getDescription(), result.get(0).getDescription());

        result = repository.search("Very", page);

        assertTrue(result.size() == 1);
        assertEquals(item.getId(), result.get(0).getId());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getDescription(), result.get(0).getDescription());
    }
}
