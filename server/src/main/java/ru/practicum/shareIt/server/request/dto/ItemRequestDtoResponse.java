package ru.practicum.shareIt.server.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareIt.server.item.dto.ItemDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class ItemRequestDtoResponse {
    private String description;
    private Long id;
    private LocalDateTime created;
    private List<ItemDtoResponse> items;
}
