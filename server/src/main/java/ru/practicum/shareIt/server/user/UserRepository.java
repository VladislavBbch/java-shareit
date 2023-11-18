package ru.practicum.shareIt.server.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareIt.server.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
