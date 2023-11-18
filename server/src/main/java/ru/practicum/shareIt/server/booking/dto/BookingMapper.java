package ru.practicum.shareIt.server.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareIt.server.booking.model.Booking;
import ru.practicum.shareIt.server.booking.model.BookingStatus;
import ru.practicum.shareIt.server.item.model.Item;
import ru.practicum.shareIt.server.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {
    public Booking toBooking(BookingDtoRequest dtoRequest, Long bookerId, BookingStatus status) {
        return Booking.builder()
                .start(dtoRequest.getStart())
                .end(dtoRequest.getEnd())
                .booker(User.builder().id(bookerId).build())
                .item(Item.builder().id(dtoRequest.getItemId()).build())
                .status(status)
                .build();
    }

    public BookingDtoResponse toBookingDto(Booking booking, Item item) {
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(BookingDtoResponse.Booker.builder().id(booking.getBooker().getId()).build())
                .item(BookingDtoResponse.Item.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .build())
                .build();
    }

    public BookingDtoResponse toBookingDto(Booking booking) {
        return toBookingDto(booking, booking.getItem());
    }

    public List<BookingDtoResponse> toBookingDto(List<Booking> bookings) {
        return bookings.stream()
                .map(this::toBookingDto)
                .collect(Collectors.toList());
    }
}
