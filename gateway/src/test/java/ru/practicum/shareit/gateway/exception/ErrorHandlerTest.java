package ru.practicum.shareit.gateway.exception;

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
import ru.practicum.shareit.gateway.item.ItemClient;
import ru.practicum.shareit.gateway.item.ItemController;
import ru.practicum.shareit.gateway.request.ItemRequestClient;
import ru.practicum.shareit.gateway.request.ItemRequestController;
import ru.practicum.shareit.gateway.request.dto.ItemRequestDtoRequest;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ItemController.class, ItemRequestController.class})
@DisplayName("ErrorHandler должен")
public class ErrorHandlerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemRequestClient requestClient;
    @MockBean
    ItemClient itemClient;

    private static final Long userId = 1L;

    @DisplayName("обрабатывать MethodArgumentNotValidException")
    @SneakyThrows
    @Test
    void shouldHandleMethodArgumentNotValidException() {
        ItemRequestDtoRequest requestDtoRequest = ItemRequestDtoRequest.builder()
                .description("   ")
                .build();

        mockMvc.perform(post("/requests")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).createRequest(userId, requestDtoRequest);
    }

    @DisplayName("обрабатывать MethodArgumentTypeMismatchException")
    @SneakyThrows
    @Test
    void shouldHandleMethodArgumentTypeMismatchException() {
        mockMvc.perform(delete("/items/{id}", "itemId")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("обрабатывать Throwable")
    @SneakyThrows
    @Test
    void shouldHandleThrowable() {
        mockMvc.perform(delete("/items/{id}", "itemId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
