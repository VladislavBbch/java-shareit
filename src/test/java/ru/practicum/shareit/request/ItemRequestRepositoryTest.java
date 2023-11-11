package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("ItemRequestRepository должен")
public class ItemRequestRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository requestRepository;

    User user1, user2;
    ItemRequest request1, request2, request3;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(null, "email1@mail.ru", "Имя1"));
        user2 = userRepository.save(new User(null, "email2@mail.ru", "Имя2"));
        request1 = requestRepository.save(new ItemRequest(null, "Описание1", null, user1));
        request2 = requestRepository.save(new ItemRequest(null, "Описание2", null, user2));
        request3 = requestRepository.save(new ItemRequest(null, "Описание3", null, user2));
    }

    @AfterEach
    void afterEach() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("возвращать все запросы по id пользователя")
    @Test
    void shouldFindAllByUserId() {
        List<ItemRequest> requests = requestRepository.findAllByUserId(user1.getId(), null);
        assertEquals(1, requests.size());
    }

    @DisplayName("возвращать все запросы кроме id пользователя")
    @Test
    void shouldFindAllByUserIdNot() {
        List<ItemRequest> requests = requestRepository.findAllByUserIdNot(user1.getId(), null);
        assertEquals(4, requests.size()); // 2 тут + 2 data.sql
    }
}
