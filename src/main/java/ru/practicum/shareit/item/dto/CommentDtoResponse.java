package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class CommentDtoResponse {
    private final Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}