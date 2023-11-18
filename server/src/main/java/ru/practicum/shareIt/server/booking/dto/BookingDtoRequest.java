package ru.practicum.shareIt.server.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class BookingDtoRequest {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}