package ru.practicum.shareit.gateway.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.gateway.booking.validation.StartBeforeEnd;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@StartBeforeEnd
public class BookingDtoRequest {
    @NotNull
    private Long itemId;
    @FutureOrPresent
    private LocalDateTime start;
    private LocalDateTime end;
}