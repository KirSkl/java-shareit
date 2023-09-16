package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplIntegrationTest {
    final EntityManager em;
    final ItemService service;
    final UserService userService;
    Long userId;
    ItemDto itemDto;

    @BeforeEach
    void loadInitial() {
        User user = new User();
        user.setName("Andrej");
        user.setEmail("witcher@mail.com");
        userId = userService.createUser(user).getId();
        itemDto = new ItemDto(null, "Hammer", "Heavy", true, null,
                null, null, null);
    }

    @Test
    void addItem() {
        service.addItem(userId, itemDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query
                .setParameter("name", itemDto.getName())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(itemDto.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void findAllMyItemsTest() {
        ItemDto itemSavedDto = service.addItem(userId, itemDto);
        int from = 0;
        int size = 10;

        ItemDto itemDtoFounded = service.findAllMyItems(userId, from, size).get(0);

        assertThat(itemDtoFounded.getId(), equalTo(itemSavedDto.getId()));
        assertThat(itemDtoFounded.getName(), equalTo(itemSavedDto.getName()));
        assertThat(itemDtoFounded.getDescription(), equalTo(itemSavedDto.getDescription()));
    }
}
