package ru.practicum.shareit.exception;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Constant;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@DisplayName("ErrorHandler должен")
public class ErrorHandlerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ItemService itemService;

    private static final Long userId = 1L;

    @DisplayName("обрабатывать ObjectNotFoundException")
    @SneakyThrows
    @Test
    void shouldHandleObjectNotFoundException() {
        Integer from = 1;
        Integer size = 2;

        when(itemService.getItems(userId, from, size))
                .thenThrow(new ObjectNotFoundException("Несуществующий id пользователя: " + userId));

        mockMvc.perform(get("/items")
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isNotFound());
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
