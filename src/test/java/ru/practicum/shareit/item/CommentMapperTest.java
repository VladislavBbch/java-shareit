package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentMapper должен")
public class CommentMapperTest {
    private final CommentMapper commentMapper = new CommentMapper();

    @DisplayName("мапить сущность в dto")
    @Test
    void shouldMapCommentToCommentDto() {
        Long itemId = 1L;
        Long ownerId = 1L;
        Long requestId = 1L;
        Item item = new Item(itemId, "Название", "Описание", true,
                new User(ownerId, "owner@mail.ru", "Владелец"), requestId);
        Long userId = 2L;
        User booker = new User(userId, "booker@mail.ru", "Бронирующий");
        Long commentId = 1L;
        Comment comment = new Comment(commentId, "Комментарий", LocalDateTime.now(), item, booker);
        List<Comment> comments = List.of(comment);

        List<CommentDtoResponse> commentDtoResponses = commentMapper.toCommentDto(comments);

        assertEquals(comment.getId(), commentDtoResponses.get(0).getId());
        assertEquals(comment.getText(), commentDtoResponses.get(0).getText());
        assertEquals(comment.getCreated(), commentDtoResponses.get(0).getCreated());
        assertEquals(comment.getAuthor().getName(), commentDtoResponses.get(0).getAuthorName());
    }

    @DisplayName("мапить dto в сущность")
    @Test
    void shouldMapCommentDtoToComment() {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder()
                .text("Комментарий")
                .build();

        Comment comment = commentMapper.toComment(userId, itemId, commentDtoRequest);

        assertEquals(commentDtoRequest.getText(), comment.getText());
        assertEquals(userId, comment.getAuthor().getId());
        assertEquals(itemId, comment.getItem().getId());
    }
}
