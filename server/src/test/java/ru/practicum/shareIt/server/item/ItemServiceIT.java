package ru.practicum.shareIt.server.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareIt.server.item.dto.CommentDtoRequest;
import ru.practicum.shareIt.server.item.dto.CommentDtoResponse;
import ru.practicum.shareIt.server.item.dto.ItemDtoRequest;
import ru.practicum.shareIt.server.item.dto.ItemDtoResponse;
import ru.practicum.shareIt.server.item.model.Comment;
import ru.practicum.shareIt.server.item.model.Item;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisplayName("ItemService должен (IT)")
public class ItemServiceIT {
    private final ItemService itemService;
    private final EntityManager em;

    private static final Long userId = 1L;
    private static final Integer from = 0;
    private static final Integer size = 20;

    @DisplayName("создавать вещь")
    @Test
    void shouldCreateItem() {
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name("Название3")
                .description("Описание3")
                .isAvailable(false)
                .build();

        ItemDtoResponse result = itemService.createItem(userId, itemDtoRequest);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query
                .setParameter("id", result.getId())
                .getSingleResult();

        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getIsAvailable(), result.getIsAvailable());
    }

    @DisplayName("возвращать вещи")
    @Test
    void shouldGetItems() {
        List<ItemDtoResponse> result = itemService.getItems(userId, from, size);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.owner.id = :ownerId", Item.class);
        List<Item> items = query
                .setParameter("ownerId", userId)
                .getResultList();

        assertEquals(items.size(), result.size());
    }

    @DisplayName("возвращать вещь по id")
    @Test
    void shouldGetItemById() {
        Long itemId = 1L;
        Long userId = 2L;

        ItemDtoResponse result = itemService.getItemById(userId, itemId);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query
                .setParameter("id", itemId)
                .getSingleResult();

        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getIsAvailable(), result.getIsAvailable());
    }

    @DisplayName("обновлять вещь")
    @Test
    void shouldUpdateItem() {
        Long itemId = 2L;
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name("ОбновленноеНазвание2")
                .description("ОбновленноеОписание2")
                .build();

        ItemDtoResponse result = itemService.updateItem(userId, itemId, itemDtoRequest);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query
                .setParameter("id", itemId)
                .getSingleResult();

        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getIsAvailable(), result.getIsAvailable());
    }

    @DisplayName("удалять вещь")
    @Test
    void shouldDeleteItem() {
        Long itemId = 3L;

        itemService.deleteItem(userId, itemId);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);

        assertThrows(NoResultException.class, () -> query.setParameter("id", itemId).getSingleResult());
    }

    @DisplayName("искать вещи")
    @Test
    void shouldSearchItems() {
        String text = "звание";
        String queryText = "%звание%";

        List<ItemDtoResponse> result = itemService.searchItems(userId, text, from, size);

        TypedQuery<Item> query = em.createQuery(
                "Select i " +
                        "from Item i " +
                        "where i.isAvailable = true " +
                        "and (lower(i.name) like lower(:text) or lower(i.description) like lower(:text)) ", Item.class);
        List<Item> items = query
                .setParameter("text", queryText)
                .getResultList();

        assertEquals(items.size(), result.size());
    }

    @DisplayName("создавать комментарий")
    @Test
    void shouldCreateComment() {
        Long itemId = 1L;
        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder()
                .text("Комментарий1")
                .build();

        CommentDtoResponse result = itemService.createComment(userId, itemId, commentDtoRequest);

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.id = :id", Comment.class);
        Comment comment = query
                .setParameter("id", result.getId())
                .getSingleResult();

        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(comment.getAuthor().getName(), result.getAuthorName());
    }
}
