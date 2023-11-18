package ru.practicum.shareit.gateway.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.gateway.item.dto.ItemDtoRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("ItemDtoRequest должен ")
public class ItemDtoRequestJsonTest {
    @Autowired
    private JacksonTester<ItemDtoRequest> jacksonTester;

    @Test
    @DisplayName("десериализовываться")
    void testItemDtoRequestDeserialize() throws Exception {
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .id(1L)
                .requestId(1L)
                .name("Название вещи")
                .description("Описание вещи")
                .isAvailable(true)
                .build();

        String dtoJson = "{\n" +
                "\"id\": 1,\n" +
                "\"requestId\": 1,\n" +
                "\"name\": \"Название вещи\",\n" +
                "\"description\": \"Описание вещи\",\n" +
                "\"available\": true\n" +
                "}";

        ItemDtoRequest dto = jacksonTester.parseObject(dtoJson);

        assertThat(dto).extracting("id").isEqualTo(itemDtoRequest.getId());
        assertThat(dto).extracting("requestId").isEqualTo(itemDtoRequest.getRequestId());
        assertThat(dto).extracting("name").isEqualTo(itemDtoRequest.getName());
        assertThat(dto).extracting("description").isEqualTo(itemDtoRequest.getDescription());
        assertThat(dto).extracting("isAvailable").isEqualTo(itemDtoRequest.getIsAvailable());
    }

}
