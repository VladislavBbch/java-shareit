package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisplayName("UserService должен (IT)")
public class UserServiceIT {
    private final UserService userService;
    private final EntityManager em;

    private static final Long userId = 1L;

    @DisplayName("создавать пользователя")
    @Test
    void shouldCreateUser() {
        UserDto userDto = UserDto.builder()
                .name("Имя Фамилия")
                .email("user@test.ru")
                .build();

        UserDto result = userService.createUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query
                .setParameter("id", result.getId())
                .getSingleResult();

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @DisplayName("возвращать пользователей")
    @Test
    void shouldGetUsers() {
        List<UserDto> result = userService.getUsers();

        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        List<User> users = query.getResultList();

        assertEquals(users.size(), result.size());
    }

    @DisplayName("возвращать пользователя по id")
    @Test
    void shouldGetUserById() {
        UserDto result = userService.getUserById(userId);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query
                .setParameter("id", userId)
                .getSingleResult();

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @DisplayName("обновлять пользователя")
    @Test
    void shouldUpdateUser() {
        UserDto userDto = UserDto.builder()
                .name("Обновленные Имя Фамилия")
                .email("updateuser@test.ru")
                .build();
        UserDto result = userService.updateUser(userId, userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query
                .setParameter("id", userId)
                .getSingleResult();

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @DisplayName("удалять пользователя")
    @Test
    void shouldDeleteUser() {
        Long userId = 3L;

        userService.deleteUser(userId);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);

        assertThrows(NoResultException.class, () -> query.setParameter("id", userId).getSingleResult());
    }
}
