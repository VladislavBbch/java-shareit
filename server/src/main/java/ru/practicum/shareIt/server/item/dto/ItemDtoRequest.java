package ru.practicum.shareIt.server.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ItemDtoRequest {
    private final Long id;
    private Long requestId;
    private String name;
    private String description;
    @JsonProperty("available")
    private Boolean isAvailable;
}
