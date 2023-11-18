package ru.practicum.shareit.gateway.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.gateway.Create;
import ru.practicum.shareit.gateway.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
public class ItemDtoRequest {
    private final Long id;
    private Long requestId;
    @Size(max = 255, groups = {Create.class, Update.class})
    @NotBlank(groups = {Create.class})
    private String name;
    @Size(max = 255, groups = {Create.class, Update.class})
    @NotBlank(groups = {Create.class})
    private String description;
    @NotNull(groups = {Create.class})
    @JsonProperty("available")
    private Boolean isAvailable;
}
