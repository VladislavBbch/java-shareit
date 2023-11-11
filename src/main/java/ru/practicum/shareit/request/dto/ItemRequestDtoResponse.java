package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

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
