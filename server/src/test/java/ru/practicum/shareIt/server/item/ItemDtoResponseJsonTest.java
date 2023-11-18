package ru.practicum.shareIt.server.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareIt.server.item.dto.ItemDtoResponse;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("ItemDtoResponse должен ")
public class ItemDtoResponseJsonTest {
    @Autowired
    private JacksonTester<ItemDtoResponse> jacksonTester;

    @Test
    @DisplayName("сериализовываться")
    void testItemDtoRequestSerialize() throws Exception {
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder()
                .id(1L)
                .name("Название вещи")
                .description("Описание вещи")
                .isAvailable(true)
                .requestId(1L)
                .build();

        String dtoJson = "{\n" +
                "\"id\": 1,\n" +
                "\"name\": \"Название вещи\",\n" +
                "\"description\": \"Описание вещи\",\n" +
                "\"available\": true,\n" +
                "\"requestId\": 1\n" +
                "}";

        JsonContent<ItemDtoResponse> json = jacksonTester.write(itemDtoResponse);

        assertThat(json).isEqualToJson(dtoJson);
    }
}
