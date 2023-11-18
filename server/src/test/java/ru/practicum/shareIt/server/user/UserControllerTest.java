package ru.practicum.shareIt.server.user;

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
import ru.practicum.shareIt.server.user.dto.UserDto;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@DisplayName("UserController должен")
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    UserService userService;

    @DisplayName("возвращать пользователей")
    @SneakyThrows
    @Test
    void shouldGetUsers() {
        List<UserDto> users = List.of();

        when(userService.getUsers())
                .thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));

        verify(userService, times(1)).getUsers();
    }

    @DisplayName("возвращать пользователя по id")
    @SneakyThrows
    @Test
    void shouldGetUserById() {
        Long userId = 1L;
        UserDto userDto = UserDto.builder().build();

        when(userService.getUserById(userId))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));

        verify(userService, times(1)).getUserById(userId);
    }

    @DisplayName("создавать пользователя")
    @SneakyThrows
    @Test
    void shouldCreateUser() {
        UserDto userDto = UserDto.builder()
                .email("email@mail.ru")
                .name("Имя")
                .build();

        when(userService.createUser(userDto))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));

        verify(userService, times(1)).createUser(userDto);
    }

    @DisplayName("обновлять пользователя")
    @SneakyThrows
    @Test
    void shouldUpdateUser() {
        Long userId = 1L;
        UserDto userDto = UserDto.builder()
                .email("email@mail.ru")
                .name("Имя")
                .build();

        when(userService.updateUser(userId, userDto))
                .thenReturn(userDto);

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));

        verify(userService, times(1)).updateUser(userId, userDto);
    }

    @DisplayName("удалять пользователя по id")
    @SneakyThrows
    @Test
    void shouldDeleteUserById() {
        Long userId = 1L;

        mockMvc.perform(delete("/users/{id}", userId)
                        .header(Constant.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(userId);
    }
}
