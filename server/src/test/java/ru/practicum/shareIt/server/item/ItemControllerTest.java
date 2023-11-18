package ru.practicum.shareIt.server.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareIt.server.Constant;
import ru.practicum.shareIt.server.item.dto.CommentDtoRequest;
import ru.practicum.shareIt.server.item.dto.CommentDtoResponse;
import ru.practicum.shareIt.server.item.dto.ItemDtoRequest;
import ru.practicum.shareIt.server.item.dto.ItemDtoResponse;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@DisplayName("ItemController должен")
public class ItemControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemService itemService;

    private static final Long userId = 1L;

    @DisplayName("возвращать вещи")
    @SneakyThrows
    @Test
    void shouldGetItems() {
        Integer from = 1;
        Integer size = 2;
        List<ItemDtoResponse> items = List.of();

        when(itemService.getItems(userId, from, size))
                .thenReturn(items);

        mockMvc.perform(get("/items")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));

        verify(itemService, times(1)).getItems(userId, from, size);
    }

    @DisplayName("возвращать вещь по id")
    @SneakyThrows
    @Test
    void shouldGetItemById() {
        Long itemId = 1L;
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder().build();

        when(itemService.getItemById(userId, itemId))
                .thenReturn(itemDtoResponse);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDtoResponse)));

        verify(itemService, times(1)).getItemById(userId, itemId);
    }

    @DisplayName("создавать вещь")
    @SneakyThrows
    @Test
    void shouldCreateItem() {
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name("Имя1")
                .description("Описание1")
                .isAvailable(true)
                .build();
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder().build();

        when(itemService.createItem(userId, itemDtoRequest))
                .thenReturn(itemDtoResponse);

        mockMvc.perform(post("/items")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDtoResponse)));

        verify(itemService, times(1)).createItem(userId, itemDtoRequest);
    }

    @DisplayName("обновлять вещь")
    @SneakyThrows
    @Test
    void shouldUpdateItem() {
        Long itemId = 1L;
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name("Имя1")
                .description("Описание1")
                .isAvailable(true)
                .build();
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder().build();

        when(itemService.updateItem(userId, itemId, itemDtoRequest))
                .thenReturn(itemDtoResponse);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDtoResponse)));

        verify(itemService, times(1)).updateItem(userId, itemId, itemDtoRequest);
    }

    @DisplayName("удалять вещь по id")
    @SneakyThrows
    @Test
    void shouldDeleteItemById() {
        Long itemId = 1L;

        mockMvc.perform(delete("/items/{id}", itemId)
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService, times(1)).deleteItem(userId, itemId);
    }

    @DisplayName("искать вещи")
    @SneakyThrows
    @Test
    void shouldSearchItems() {
        Integer from = 1;
        Integer size = 2;
        String text = "текст";
        List<ItemDtoResponse> items = List.of();

        when(itemService.searchItems(userId, text, from, size))
                .thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));

        verify(itemService, times(1)).searchItems(userId, text, from, size);
    }

    @DisplayName("создавать комментарий")
    @SneakyThrows
    @Test
    void shouldCreateComment() {
        Long itemId = 1L;
        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder()
                .text("Комментарий")
                .build();
        CommentDtoResponse commentDtoResponse = CommentDtoResponse.builder().build();

        when(itemService.createComment(userId, itemId, commentDtoRequest))
                .thenReturn(commentDtoResponse);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentDtoResponse)));

        verify(itemService, times(1)).createComment(userId, itemId, commentDtoRequest);
    }
}
