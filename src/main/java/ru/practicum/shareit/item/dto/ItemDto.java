package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder(toBuilder = true)
public class ItemDto {
    private final Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    @JsonProperty("available")
    private Boolean isAvailable;
}
