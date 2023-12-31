package ru.practicum.shareIt.server.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareIt.server.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class BookingDtoResponse {
    private final Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private Booker booker;
    private Item item;

    @Data
    @Builder(toBuilder = true)
    static class Booker {
        Long id;
    }

    @Data
    @Builder(toBuilder = true)
    static class Item {
        Long id;
        String name;
    }
}
