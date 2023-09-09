package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

import static java.util.Map.entry;

@Repository
@RequiredArgsConstructor
public class DatabaseItemRepository implements ItemRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate parameterJdbcTemplate;

    @Override
    public Optional<Item> getById(Long id) {
        SqlRowSet itemRow = parameterJdbcTemplate.queryForRowSet("SELECT * FROM ITEMS WHERE ID = :id", Map.of("id", id));
        if (itemRow.next()) {
            return Optional.of(mapRowToItem(itemRow));
        }
        return Optional.empty();
    }

    @Override
    public Item create(Item item) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("ITEMS")
                .usingGeneratedKeyColumns("ID");
        Long id = simpleJdbcInsert.executeAndReturnKey(Map.ofEntries(
                entry("NAME", item.getName()),
                entry("DESCRIPTION", item.getDescription()),
                entry("IS_AVAILABLE", item.getIsAvailable()),
                entry("USER_ID", item.getOwner())
        )).longValue();
        return item.toBuilder()
                .id(id)
                .build();
    }

    @Override
    public List<Item> read(Long userId) {
        List<Item> items = new ArrayList<>();
        SqlRowSet itemRow = parameterJdbcTemplate.queryForRowSet("SELECT * FROM ITEMS WHERE USER_ID = :userId",
                Map.of("userId", userId));
        while (itemRow.next()) {
            items.add(mapRowToItem(itemRow));
        }
        return items;
    }

    @Override
    public int update(Item item) {
        if (!(item.getIsAvailable() == null
                && (item.getName() == null || item.getName().isBlank())
                && (item.getDescription() == null || item.getDescription().isBlank()))) {
            Map<String, Object> mapOfEntries = new HashMap<>();
            StringBuilder sqlQuery = new StringBuilder("UPDATE ITEMS SET ");
            if (item.getName() != null && !item.getName().isBlank()) {
                sqlQuery.append("NAME = :name");
                mapOfEntries.put("name", item.getName());
            }
            if (item.getDescription() != null && !item.getDescription().isBlank()) {
                if (!sqlQuery.substring(sqlQuery.length() - 1).equals(" ")) {
                    sqlQuery.append(", ");
                }
                sqlQuery.append("DESCRIPTION = :description");
                mapOfEntries.put("description", item.getDescription());
            }
            if (item.getIsAvailable() != null) {
                if (!sqlQuery.substring(sqlQuery.length() - 1).equals(" ")) {
                    sqlQuery.append(", ");
                }
                sqlQuery.append("IS_AVAILABLE = :isAvailable");
                mapOfEntries.put("isAvailable", item.getIsAvailable());
            }
            sqlQuery.append(" WHERE ID = :id AND USER_ID = :userId");
            mapOfEntries.put("id", item.getId());
            mapOfEntries.put("userId", item.getOwner());
            return parameterJdbcTemplate.update(sqlQuery.toString(), mapOfEntries);
        }
        return -1;
    }

    @Override
    public void delete(Long userId, Long id) {
        parameterJdbcTemplate.update("DELETE FROM ITEMS WHERE ID = :id AND USER_ID = :userId",
                Map.of("id", id,
                        "userId", userId));
    }

    @Override
    public List<Item> searchItems(Long userId, String text) {
        text = "%" + text + "%";
        List<Item> items = new ArrayList<>();
        SqlRowSet itemRow = parameterJdbcTemplate.queryForRowSet(
                "SELECT * FROM ITEMS " +
                        "WHERE IS_AVAILABLE = true " +
                        "AND (LOWER(NAME) LIKE LOWER(:text) OR LOWER(DESCRIPTION) LIKE LOWER(:text))",
                Map.of("userId", userId,
                        "text", text));

        while (itemRow.next()) {
            items.add(mapRowToItem(itemRow));
        }
        return items;
    }

    private Item mapRowToItem(SqlRowSet itemRow) {
        return Item.builder()
                .id(itemRow.getLong("ID"))
                .name(itemRow.getString("NAME"))
                .description(itemRow.getString("DESCRIPTION"))
                .isAvailable(itemRow.getBoolean("IS_AVAILABLE"))
                .build();
    }
}
