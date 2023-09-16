package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommentRepository repository;

    @Test
    void testFindAllByItemOk() {
        User user = userRepository.save(new User(null, "John", "john@doe.com"));
        User userAnother = userRepository.save(new User(null, "Adam", "adam@smith.com"));

        Item item = itemRepository.save(new Item(null, "Hammer", "Very big", true,
                user.getId(), null));
        Item itemAnother = itemRepository.save(new Item(null, "Hood", "Black", true,
                user.getId(), null));

        Comment comment = repository.save(new Comment(null, "Хорошая вещь", item, userAnother,
                LocalDateTime.now()));
        Comment commentAnother = repository.save(new Comment(null, "Не очень хорошая вещь", itemAnother,
                userAnother, LocalDateTime.now()));

        var result = repository.findAllByItem(item);

        assertEquals(1, result.size());
        assertEquals(comment.getId(), result.get(0).getId());
        assertEquals(comment.getText(), result.get(0).getText());
        assertEquals(comment.getAuthor(), result.get(0).getAuthor());
    }

}
