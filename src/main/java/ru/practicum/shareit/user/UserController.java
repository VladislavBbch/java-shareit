package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        log.info("Начало обработки запроса по получению пользователя: {}", id);
        UserDto user = userService.getUserById(id);
        log.info("Окончание обработки запроса по получению пользователя");
        return user;
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Validated({Create.class}) UserDto user) {
        log.info("Начало обработки запроса на создание пользователя: {}", user);
        UserDto newUser = userService.createUser(user);
        log.info("Окончание обработки запроса на создание пользователя");
        return newUser;
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @Validated({Update.class}) @RequestBody UserDto user) {
        log.info("Начало обработки запроса на обновление пользователя: {}", id);
        UserDto existingUser = userService.updateUser(id, user);
        log.info("Окончание обработки запроса на обновление пользователя");
        return existingUser;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("Начало обработки запроса на удаление пользователя: {}", id);
        userService.deleteUser(id);
        log.info("Окончание обработки запроса на удаление пользователя");
    }
}
