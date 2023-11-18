package ru.practicum.shareit.gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.Create;
import ru.practicum.shareit.gateway.Update;
import ru.practicum.shareit.gateway.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Начало обработки запроса на получение всех вещей");
        ResponseEntity<Object> users = userClient.getUsers();
        log.info("Окончание обработки запроса на получение всех вещей");
        return users;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("Начало обработки запроса по получению пользователя: {}", id);
        ResponseEntity<Object> user = userClient.getUserById(id);
        log.info("Окончание обработки запроса по получению пользователя");
        return user;
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Validated({Create.class}) UserDto user) {
        log.info("Начало обработки запроса на создание пользователя: {}", user);
        ResponseEntity<Object> newUser = userClient.createUser(user);
        log.info("Окончание обработки запроса на создание пользователя");
        return newUser;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @Validated({Update.class}) @RequestBody UserDto user) {
        log.info("Начало обработки запроса на обновление пользователя: {}", id);
        ResponseEntity<Object> existingUser = userClient.updateUser(id, user);
        log.info("Окончание обработки запроса на обновление пользователя");
        return existingUser;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        log.info("Начало обработки запроса на удаление пользователя: {}", id);
        ResponseEntity<Object> result = userClient.deleteUser(id);
        log.info("Окончание обработки запроса на удаление пользователя");
        return result;
    }
}
