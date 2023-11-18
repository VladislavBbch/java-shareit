package ru.practicum.shareit.gateway.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.gateway.user.dto.UserDto;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@DisplayName("UserController должен")
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    UserClient userClient;

    @DisplayName("валидировать email пользователя (size) при создании пользователя")
    @SneakyThrows
    @Test
    void shouldValidateEmailSize_CreateUser() {
        String s = "1";
        UserDto userDto = UserDto.builder()
                .email(s.repeat(256) + "@mail.ru")
                .name("Имя")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(userDto);
    }

    @DisplayName("валидировать email пользователя (not blank) при создании пользователя")
    @SneakyThrows
    @Test
    void shouldValidateEmailNotBlank_CreateUser() {
        UserDto userDto = UserDto.builder()
                .email(" ")
                .name("Имя")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(userDto);
    }

    @DisplayName("валидировать email пользователя (email) при создании пользователя")
    @SneakyThrows
    @Test
    void shouldValidateEmailIsEmail_CreateUser() {
        UserDto userDto = UserDto.builder()
                .email("email")
                .name("Имя")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(userDto);
    }

    @DisplayName("валидировать имя пользователя (size) при создании пользователя")
    @SneakyThrows
    @Test
    void shouldValidateDescriptionSize_CreateUser() {
        String s = "1";
        UserDto userDto = UserDto.builder()
                .email("email@mail.ru")
                .name(s.repeat(256))
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(userDto);
    }

    @DisplayName("валидировать имя пользователя (not blank) при создании пользователя")
    @SneakyThrows
    @Test
    void shouldValidateDescriptionNotBlank_CreateUser() {
        UserDto userDto = UserDto.builder()
                .email("email@mail.ru")
                .name(" ")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(userDto);
    }

    @DisplayName("валидировать email пользователя (size) при обновлении пользователя")
    @SneakyThrows
    @Test
    void shouldValidateEmailSize_UpdateUser() {
        Long userId = 1L;
        String s = "1";
        UserDto userDto = UserDto.builder()
                .email(s.repeat(256) + "@mail.ru")
                .name("Имя")
                .build();

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(userId, userDto);
    }

    @DisplayName("валидировать email пользователя (email) при обновлении пользователя")
    @SneakyThrows
    @Test
    void shouldValidateEmailIsEmail_UpdateUser() {
        Long userId = 1L;
        UserDto userDto = UserDto.builder()
                .email("email")
                .name("Имя")
                .build();

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(userId, userDto);
    }

    @DisplayName("валидировать имя пользователя (size) при обновлении пользователя")
    @SneakyThrows
    @Test
    void shouldValidateDescriptionSize_UpdateUser() {
        Long userId = 1L;
        String s = "1";
        UserDto userDto = UserDto.builder()
                .email("email@mail.ru")
                .name(s.repeat(256))
                .build();

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(userId, userDto);
    }
}
