package ru.practicum.shareit.gateway.request;

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
import ru.practicum.shareit.gateway.request.dto.ItemRequestDtoRequest;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@DisplayName("ItemRequestController должен")
public class ItemRequestControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemRequestClient requestClient;

    private static final Long userId = 1L;

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

        verify(requestClient, never()).createRequest(userId, requestDtoRequest);
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

        verify(requestClient, never()).createRequest(userId, requestDtoRequest);
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

        verify(requestClient, never()).getOtherUsersItemRequests(userId, from, size);
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

        verify(requestClient, never()).getOtherUsersItemRequests(userId, from, size);
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

        verify(requestClient, never()).getOtherUsersItemRequests(userId, from, size);
    }
}
