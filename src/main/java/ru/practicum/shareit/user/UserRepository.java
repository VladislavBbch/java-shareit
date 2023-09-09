package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User create(User user);

    List<User> read();

    User update(User user);

    Optional<User> getById(Long id);

    Optional<User> getByEmail(String email);

    void delete(Long id);
}
