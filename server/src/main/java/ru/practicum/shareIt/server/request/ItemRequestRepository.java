package ru.practicum.shareIt.server.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareIt.server.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByUserId(Long userId, Sort sort);

    List<ItemRequest> findAllByUserIdNot(Long userId, Pageable pageable);
}
