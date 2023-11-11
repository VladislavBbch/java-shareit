package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Constant;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@DisplayName("ItemRequestController должен")
public class ItemRequestControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemRequestService requestService;

    private static final Long userId = 1L;

    @DisplayName("создавать бронирование")
    @SneakyThrows
    @Test
    void shouldCreateItemRequest() {
        ItemRequestDtoRequest requestDtoRequest = ItemRequestDtoRequest.builder()
                .description("Описание")
                .build();
        ItemRequestDtoResponse requestDtoResponse = ItemRequestDtoResponse.builder().build();

        when(requestService.createRequest(userId, requestDtoRequest))
                .thenReturn(requestDtoResponse);

        mockMvc.perform(post("/requests")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDtoResponse)));

        verify(requestService, times(1)).createRequest(userId, requestDtoRequest);
    }

    @DisplayName("валидировать описание запроса (size) при создании запроса")
    @SneakyThrows
    @Test
    void shouldValidateDescriptionSize_CreateItemRequest() {
        String s = "1";
        ItemRequestDtoRequest requestDtoRequest = ItemRequestDtoRequest.builder()
                .description(s.repeat(1001))
                .build();

        mockMvc.perform(post("/requests")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).createRequest(userId, requestDtoRequest);
    }

    @DisplayName("валидировать описание запроса (not blank) при создании запроса")
    @SneakyThrows
    @Test
    void shouldValidateDescriptionNotBlank_CreateItemRequest() {
        ItemRequestDtoRequest requestDtoRequest = ItemRequestDtoRequest.builder()
                .description(" ")
                .build();

        mockMvc.perform(post("/requests")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).createRequest(userId, requestDtoRequest);
    }

    @DisplayName("возвращать запросы вещей")
    @SneakyThrows
    @Test
    void shouldGetItemRequests() {
        List<ItemRequestDtoResponse> requests = List.of();

        when(requestService.getItemRequests(userId))
                .thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requests)));

        verify(requestService, times(1)).getItemRequests(userId);
    }

    @DisplayName("возвращать запросы вещей других пользователей")
    @SneakyThrows
    @Test
    void shouldGetOtherUsersItemRequests() {
        Integer from = 1;
        Integer size = 2;
        List<ItemRequestDtoResponse> requests = List.of();

        when(requestService.getOtherUsersItemRequests(userId, from, size))
                .thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requests)));

        verify(requestService, times(1)).getOtherUsersItemRequests(userId, from, size);
    }

    @DisplayName("валидировать значение from при возвращении запросов вещей других пользователей")
    @SneakyThrows
    @Test
    void shouldValidateFrom_GetOtherUsersItemRequests() {
        Integer from = -1;
        Integer size = 2;

        mockMvc.perform(get("/requests/all")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).getOtherUsersItemRequests(userId, from, size);
    }

    @DisplayName("валидировать значение size (min) при возвращении запросов вещей других пользователей")
    @SneakyThrows
    @Test
    void shouldValidateSizeMin_GetOtherUsersItemRequests() {
        Integer from = 1;
        Integer size = 0;

        mockMvc.perform(get("/requests/all")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).getOtherUsersItemRequests(userId, from, size);
    }

    @DisplayName("валидировать значение size (max) при возвращении запросов вещей других пользователей")
    @SneakyThrows
    @Test
    void shouldValidateSizeMax_GetOtherUsersItemRequests() {
        Integer from = 1;
        Integer size = 31;

        mockMvc.perform(get("/requests/all")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).getOtherUsersItemRequests(userId, from, size);
    }

    @DisplayName("возвращать запрос по id")
    @SneakyThrows
    @Test
    void shouldGetItemRequestById() {
        Long requestId = 1L;
        ItemRequestDtoResponse requestDtoResponse = ItemRequestDtoResponse.builder().build();

        when(requestService.getItemRequestById(userId, requestId))
                .thenReturn(requestDtoResponse);

        mockMvc.perform(get("/requests/{id}", requestId)
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDtoResponse)));

        verify(requestService, times(1)).getItemRequestById(userId, requestId);
    }
}
