package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemsByOwnerIdOrderById(Long ownerId, Pageable page);

    List<Item> findAllByRequestId(Long requestId);

    @Query("select i from Item i " +
            "where ((upper(i.name) like upper(concat('%', ?1, '%'))) or " +
            "(upper(i.description) like upper(concat('%', ?1, '%'))))" +
            "and i.isAvailable = true")
    List<Item> search(String text, Pageable page);
}
