package ru.practicum.shareIt.server.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareIt.server.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderById(Long ownerId, Pageable pageable);

    @Query("select i " +
            "from Item i " +
            "where i.isAvailable = true " +
            "and (lower(i.name) like lower(?1) or lower(i.description) like lower(?1))")
    List<Item> findAllByIsAvailableAndTextInNameOrDescription(String text, Pageable pageable);

    void deleteByIdAndOwnerId(Long id, Long ownerId);

    List<Item> findAllByRequestId(Long requestId);

    List<Item> findAllByRequestIdIn(List<Long> requestIds);
}