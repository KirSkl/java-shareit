package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    public List<Item> findItemsByOwnerId(Long ownerId);

    @Query("select i from Item i " +
            "where (i.name like upper('%?1%') or i.description like upper('%?1%')) " +
            "and i.isAvailable = true")
    public List<Item> search(String text);
}
