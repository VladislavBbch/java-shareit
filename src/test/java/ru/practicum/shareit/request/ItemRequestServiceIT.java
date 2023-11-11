package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisplayName("ItemRequestService должен (IT)")
public class ItemRequestServiceIT {
    private final ItemRequestService requestService;
    private final EntityManager em;

    private static final Long userId = 1L;
    private static final Integer from = 0;
    private static final Integer size = 20;

    @DisplayName("создавать запрос")
    @Test
    void shouldCreateItemRequest() {
        ItemRequestDtoRequest requestDtoRequest = ItemRequestDtoRequest.builder()
                .description("Описание")
                .build();

        ItemRequestDtoResponse result = requestService.createRequest(userId, requestDtoRequest);

        TypedQuery<ItemRequest> query = em.createQuery("Select r from ItemRequest r where r.id = :id", ItemRequest.class);
        ItemRequest request = query
                .setParameter("id", result.getId())
                .getSingleResult();

        assertEquals(request.getId(), result.getId());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getUser().getId(), userId);
    }

    @DisplayName("возвращать запросы пользователя")
    @Test
    void shouldGetItemRequests() {
        List<ItemRequestDtoResponse> result = requestService.getItemRequests(userId);

        TypedQuery<ItemRequest> query = em.createQuery("Select r from ItemRequest r where r.user.id = :userId", ItemRequest.class);
        List<ItemRequest> requests = query
                .setParameter("userId", userId)
                .getResultList();

        assertEquals(requests.size(), result.size());
    }

    @DisplayName("возвращать запросы других пользователей")
    @Test
    void shouldGetOtherUsersItemRequests() {
        List<ItemRequestDtoResponse> result = requestService.getOtherUsersItemRequests(userId, from, size);

        TypedQuery<ItemRequest> query = em.createQuery("Select r from ItemRequest r where r.user.id != :userId", ItemRequest.class);
        List<ItemRequest> requests = query
                .setParameter("userId", userId)
                .getResultList();

        assertEquals(requests.size(), result.size());
    }

    @DisplayName("возвращать запрос по id")
    @Test
    void shouldGetItemRequestById() {
        Long requestId = 1L;

        ItemRequestDtoResponse result = requestService.getItemRequestById(userId, requestId);

        TypedQuery<ItemRequest> query = em.createQuery("Select r from ItemRequest r where r.id = :id", ItemRequest.class);
        ItemRequest request = query
                .setParameter("id", result.getId())
                .getSingleResult();

        assertEquals(request.getId(), result.getId());
        assertEquals(request.getDescription(), result.getDescription());
    }
}
