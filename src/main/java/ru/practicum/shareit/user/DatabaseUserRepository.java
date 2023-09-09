package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Map.entry;

@Repository
@RequiredArgsConstructor
public class DatabaseUserRepository implements UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate parameterJdbcTemplate;

    @Override
    public Optional<User> getById(Long id) {
        SqlRowSet userRow = parameterJdbcTemplate.queryForRowSet("SELECT * FROM USERS WHERE ID = :id", Map.of("id", id));
        if (userRow.next()) {
            return Optional.of(mapRowToUser(userRow));
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> getByEmail(String email) {
        SqlRowSet userRow = parameterJdbcTemplate.queryForRowSet("SELECT * FROM USERS WHERE EMAIL = :email", Map.of("email", email));
        if (userRow.next()) {
            return Optional.of(mapRowToUser(userRow));
        }
        return Optional.empty();
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("ID");
        Long id = simpleJdbcInsert.executeAndReturnKey(Map.ofEntries(
                entry("EMAIL", user.getEmail()),
                entry("NAME", user.getName())
        )).longValue();
        return user.toBuilder()
                .id(id)
                .build();
    }

    @Override
    public List<User> read() {
        List<User> users = new ArrayList<>();
        SqlRowSet userRow = jdbcTemplate.queryForRowSet("SELECT * FROM USERS");
        while (userRow.next()) {
            users.add(mapRowToUser(userRow));
        }
        return users;
    }

    @Override
    public User update(User user) {
        if ((user.getEmail() == null || user.getEmail().isBlank())
                && (user.getName() == null || user.getName().isBlank())) {
            return getById(user.getId()).get();
        } else if (user.getEmail() == null || user.getEmail().isBlank()) {
            parameterJdbcTemplate.update(
                    "UPDATE USERS SET NAME = :name WHERE ID = :id",
                    Map.ofEntries(
                            entry("name", user.getName()),
                            entry("id", user.getId())
                    ));
        } else if (user.getName() == null || user.getName().isBlank()) {
            parameterJdbcTemplate.update(
                    "UPDATE USERS SET EMAIL = :email WHERE ID = :id",
                    Map.ofEntries(
                            entry("email", user.getEmail()),
                            entry("id", user.getId())
                    ));
        } else {
            parameterJdbcTemplate.update(
                    "UPDATE USERS SET EMAIL = :email, NAME = :name WHERE ID = :id",
                    Map.ofEntries(
                            entry("email", user.getEmail()),
                            entry("name", user.getName()),
                            entry("id", user.getId())
                    ));
            return user;
        }
        return getById(user.getId()).get();
    }

    @Override
    public void delete(Long id) {
        parameterJdbcTemplate.update("DELETE FROM USERS WHERE ID = :id", Map.of("id", id));
    }

    private User mapRowToUser(SqlRowSet userRow) {
        return User.builder()
                .id(userRow.getLong("ID"))
                .email(userRow.getString("EMAIL"))
                .name(userRow.getString("NAME"))
                .build();
    }
}
