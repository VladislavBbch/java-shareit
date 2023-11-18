package ru.practicum.shareit.gateway.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.gateway.Constant;
import ru.practicum.shareit.gateway.item.dto.CommentDtoRequest;
import ru.practicum.shareit.gateway.item.dto.ItemDtoRequest;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@DisplayName("ItemController должен")
public class ItemControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemClient itemClient;

    private static final Long userId = 1L;

    @DisplayName("валидировать значение from при возвращении вещей")
    @SneakyThrows
    @Test
    void shouldValidateFrom_GetItems() {
        Integer from = -1;
        Integer size = 2;

        mockMvc.perform(get("/items")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItems(userId, from, size);
    }

    @DisplayName("валидировать значение size (min) при возвращении вещей")
    @SneakyThrows
    @Test
    void shouldValidateSizeMin_GetItems() {
        Integer from = 1;
        Integer size = 0;

        mockMvc.perform(get("/items")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItems(userId, from, size);
    }

    @DisplayName("валидировать значение size (max) при возвращении вещей")
    @SneakyThrows
    @Test
    void shouldValidateSizeMax_GetItems() {
        Integer from = 1;
        Integer size = 31;

        mockMvc.perform(get("/items")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItems(userId, from, size);
    }

    @DisplayName("валидировать название вещи (size) при создании вещи")
    @SneakyThrows
    @Test
    void shouldValidateNameSize_CreateItem() {
        String s = "1";
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name(s.repeat(256))
                .description("Описание1")
                .isAvailable(true)
                .build();

        mockMvc.perform(post("/items")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(userId, itemDtoRequest);
    }

    @DisplayName("валидировать название вещи (not blank) при создании вещи")
    @SneakyThrows
    @Test
    void shouldValidateNameNotBlank_CreateItem() {
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name(" ")
                .description("Описание1")
                .isAvailable(true)
                .build();

        mockMvc.perform(post("/items")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(userId, itemDtoRequest);
    }

    @DisplayName("валидировать описание вещи (size) при создании вещи")
    @SneakyThrows
    @Test
    void shouldValidateDescriptionSize_CreateItem() {
        String s = "1";
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name("Имя1")
                .description(s.repeat(256))
                .isAvailable(true)
                .build();

        mockMvc.perform(post("/items")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(userId, itemDtoRequest);
    }

    @DisplayName("валидировать описание вещи (not blank) при создании вещи")
    @SneakyThrows
    @Test
    void shouldValidateDescriptionNotBlank_CreateItem() {
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name("Имя1")
                .description(" ")
                .isAvailable(true)
                .build();

        mockMvc.perform(post("/items")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(userId, itemDtoRequest);
    }

    @DisplayName("валидировать доступность вещи при создании вещи")
    @SneakyThrows
    @Test
    void shouldValidateAvailability_CreateItem() {
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name("Имя1")
                .description("Описание1")
                .build();

        mockMvc.perform(post("/items")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(userId, itemDtoRequest);
    }

    @DisplayName("валидировать название вещи (size) при обновлении вещи")
    @SneakyThrows
    @Test
    void shouldValidateNameSize_UpdateItem() {
        Long itemId = 1L;
        String s = "1";
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name(s.repeat(256))
                .description("Описание1")
                .isAvailable(true)
                .build();

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).updateItem(userId, itemId, itemDtoRequest);
    }

    @DisplayName("валидировать описание вещи (size) при обновлении вещи")
    @SneakyThrows
    @Test
    void shouldValidateDescriptionSize_UpdateItem() {
        Long itemId = 1L;
        String s = "1";
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name("Имя1")
                .description(s.repeat(256))
                .isAvailable(true)
                .build();

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).updateItem(userId, itemId, itemDtoRequest);
    }

    @DisplayName("валидировать значение from при поиске вещей")
    @SneakyThrows
    @Test
    void shouldValidateFrom_SearchItems() {
        Integer from = -1;
        Integer size = 2;
        String text = "текст";

        mockMvc.perform(get("/items/search")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).searchItems(userId, text, from, size);
    }

    @DisplayName("валидировать значение size (min) при поиске вещей")
    @SneakyThrows
    @Test
    void shouldValidateSizeMin_SearchItems() {
        Integer from = 1;
        Integer size = 0;
        String text = "текст";

        mockMvc.perform(get("/items/search")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).searchItems(userId, text, from, size);
    }

    @DisplayName("валидировать значение size (max) при поиске вещей")
    @SneakyThrows
    @Test
    void shouldValidateSizeMax_SearchItems() {
        Integer from = 1;
        Integer size = 31;
        String text = "текст";

        mockMvc.perform(get("/items/search")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).searchItems(userId, text, from, size);
    }

    @DisplayName("валидировать текст комментария (size) при создании комментария")
    @SneakyThrows
    @Test
    void shouldValidateTextSize_CreateComment() {
        Long itemId = 1L;
        String s = "1";
        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder()
                .text(s.repeat(5001))
                .build();

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createComment(userId, itemId, commentDtoRequest);
    }
}
