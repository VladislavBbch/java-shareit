package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemRequestMapper должен")
public class ItemRequestMapperTest {
    @InjectMocks
    ItemRequestMapper requestMapper;
    @Mock
    ItemMapper itemMapper;

    @DisplayName("мапить сущность в dto")
    @Test
    void shouldMapItemRequestToItemRequestDto() {
        Long userId = 1L;
        User user = new User(userId, "user@mail.ru", "Пользователь");
        Long requestId = 1L;
        ItemRequest request = new ItemRequest(requestId, "Описание", LocalDateTime.now(), user);

        List<ItemRequestDtoResponse> requestDtoResponses = requestMapper.toItemRequestDto(List.of(request), Map.of());

        assertEquals(request.getId(), requestDtoResponses.get(0).getId());
        assertEquals(request.getDescription(), requestDtoResponses.get(0).getDescription());
        assertEquals(request.getCreated(), requestDtoResponses.get(0).getCreated());
        assertEquals(List.of(), requestDtoResponses.get(0).getItems());
    }

    @DisplayName("мапить dto в сущность")
    @Test
    void shouldMapItemRequestDtoToItemRequest() {
        Long userId = 1L;
        ItemRequestDtoRequest itemRequestDtoRequest = ItemRequestDtoRequest.builder()
                .description("Описание")
                .build();

        ItemRequest request = requestMapper.toItemRequest(itemRequestDtoRequest, userId);

        assertEquals(itemRequestDtoRequest.getDescription(), request.getDescription());
        assertEquals(userId, request.getUser().getId());
    }
}
