package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("CommentRepository должен")
public class CommentRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CommentRepository commentRepository;

    User user1;
    Item item1, item2, item3, item4, item5;
    Comment comment1, comment2, comment3;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(null, "email1@mail.ru", "Имя1"));
        item1 = itemRepository.save(new Item(null, "Название1", "Описание1", true, user1, null));
        item2 = itemRepository.save(new Item(null, "Название2", "Описание2", false, user1, null));
        item3 = itemRepository.save(new Item(null, "Название3", "Описание3", true, user1, null));
        item4 = itemRepository.save(new Item(null, "Название4", "Описание4", false, user1, null));
        item5 = itemRepository.save(new Item(null, "Название5", "Описание5", true, user1, null));
        comment1 = commentRepository.save(new Comment(null, "Комментарий1", null, item1, user1));
        comment2 = commentRepository.save(new Comment(null, "Комментарий2", null, item2, user1));
        comment3 = commentRepository.save(new Comment(null, "Комментарий3", null, item2, user1));
        comment3 = commentRepository.save(new Comment(null, "Комментарий4", null, item3, user1));
        comment3 = commentRepository.save(new Comment(null, "Комментарий5", null, item3, user1));
        comment3 = commentRepository.save(new Comment(null, "Комментарий6", null, item3, user1));
    }

    @AfterEach
    void afterEach() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("возвращать все комментарии по id вещи")
    @Test
    void shouldFindAllByItemId() {
        List<Comment> comments = commentRepository.findAllByItemId(item2.getId());
        assertEquals(2, comments.size());
    }

    @DisplayName("возвращать все комментарии по списку id вещи")
    @Test
    void shouldFindAllByItemIdIn() {
        List<Comment> comments = commentRepository.findAllByItemIdIn(List.of(item1.getId(), item3.getId()));
        assertEquals(4, comments.size());
    }
}
