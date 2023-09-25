package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class ItemDtoResponse {
    private final Long id;
    private String name;
    private String description;
    @JsonProperty("available")
    private Boolean isAvailable;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<CommentDtoResponse> comments;

    @Data
    @Builder(toBuilder = true)
    static class Booking {
        Long id;
        Long bookerId;
    }
}
