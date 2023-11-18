package ru.practicum.shareIt.server.item;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareIt.server.item.model.Item;
import ru.practicum.shareIt.server.request.ItemRequestRepository;
import ru.practicum.shareIt.server.request.model.ItemRequest;
import ru.practicum.shareIt.server.user.UserRepository;
import ru.practicum.shareIt.server.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("ItemRepository должен")
public class ItemRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository requestRepository;
    @Autowired
    ItemRepository itemRepository;

    User user1, user2;
    ItemRequest request1, request2, request3;
    Item item1, item2, item3, item4, item5;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(null, "email1@mail.ru", "Имя1"));
        user2 = userRepository.save(new User(null, "email2@mail.ru", "Имя2"));
        request1 = requestRepository.save(new ItemRequest(null, "Описание1", null, user1));
        request2 = requestRepository.save(new ItemRequest(null, "Описание2", null, user2));
        request3 = requestRepository.save(new ItemRequest(null, "Описание3", null, user2));
        item1 = itemRepository.save(new Item(null, "Название1", "Описание1", true, user1, request1.getId()));
        item2 = itemRepository.save(new Item(null, "Название2", "Описание2", false, user1, request2.getId()));
        item3 = itemRepository.save(new Item(null, "Название3", "Описание3", true, user1, request2.getId()));
        item4 = itemRepository.save(new Item(null, "Название4", "Описание4", false, user2, request3.getId()));
        item5 = itemRepository.save(new Item(null, "Название5", "Описание5", true, user2, request3.getId()));
    }

    @AfterEach
    void afterEach() {
        requestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("возвращать все вещи по id владельца")
    @Test
    void shouldFindAllByOwnerIdOrderById() {
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(user1.getId(), null);
        assertEquals(3, items.size());
    }

    @DisplayName("искать все доступные вещи по тексту в названии или описании")
    @Test
    void shouldFindAllByIsAvailableAndTextInNameOrDescription() {
        List<Item> items = itemRepository.findAllByIsAvailableAndTextInNameOrDescription("%назван%", null);
        assertEquals(6, items.size()); // 3 тут + 3 data.sql
    }

    @DisplayName("возвращать все вещи по id запроса")
    @Test
    void shouldFindAllByRequestId() {
        List<Item> items = itemRepository.findAllByRequestId(request2.getId());
        assertEquals(2, items.size());
    }

    @DisplayName("возвращать все вещи по списку id запроса")
    @Test
    void shouldFindAllByRequestIdIn() {
        List<Item> items = itemRepository.findAllByRequestIdIn(List.of(request1.getId(), request2.getId()));
        assertEquals(3, items.size());
    }

    @DisplayName("удалять вещь по id и id владельца")
    @Test
    void shouldDeleteByIdAndOwnerId() {
        Integer count = itemRepository.findAll().size();
        itemRepository.deleteByIdAndOwnerId(item1.getId(), user1.getId());
        List<Item> items = itemRepository.findAll();
        assertEquals(count - 1, items.size());
    }
}
