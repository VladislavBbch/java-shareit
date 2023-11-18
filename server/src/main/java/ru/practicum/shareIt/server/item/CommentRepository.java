package ru.practicum.shareIt.server.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareIt.server.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItemId(Long itemId);

    List<Comment> findAllByItemIdIn(List<Long> itemIds);
}
