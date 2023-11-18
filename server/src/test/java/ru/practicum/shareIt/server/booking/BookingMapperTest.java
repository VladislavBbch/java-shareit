package ru.practicum.shareIt.server.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareIt.server.booking.dto.BookingDtoRequest;
import ru.practicum.shareIt.server.booking.dto.BookingDtoResponse;
import ru.practicum.shareIt.server.booking.dto.BookingMapper;
import ru.practicum.shareIt.server.booking.model.Booking;
import ru.practicum.shareIt.server.booking.model.BookingStatus;
import ru.practicum.shareIt.server.item.model.Item;
import ru.practicum.shareIt.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingMapper должен")
public class BookingMapperTest {
    private final BookingMapper bookingMapper = new BookingMapper();

    @DisplayName("мапить сущность в dto")
    @Test
    void shouldMapBookingToBookingDto() {
        Long userId = 1L;
        User booker = new User(userId, "booker@mail.ru", "Бронирующий");
        Long itemId = 1L;
        Long ownerId = 2L;
        Item item = new Item(itemId, "Название", "Описание", true, new User(ownerId, "owner@mail.ru", "Владелец"), null);
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingStatus status = BookingStatus.WAITING;
        Booking booking = new Booking(bookingId, start, end, status, booker, item);

        List<BookingDtoResponse> bookingDtoResponses = bookingMapper.toBookingDto(List.of(booking));

        assertEquals(booking.getId(), bookingDtoResponses.get(0).getId());
        assertEquals(booking.getStart(), bookingDtoResponses.get(0).getStart());
        assertEquals(booking.getEnd(), bookingDtoResponses.get(0).getEnd());
        assertEquals(booking.getStatus(), bookingDtoResponses.get(0).getStatus());
    }

    @DisplayName("мапить dto в сущность")
    @Test
    void shouldMapBookingDtoToBooking() {
        Long itemId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(itemId)
                .start(start)
                .end(end)
                .build();
        Long userId = 1L;
        BookingStatus status = BookingStatus.WAITING;

        Booking booking = bookingMapper.toBooking(bookingDtoRequest, userId, status);

        assertEquals(bookingDtoRequest.getStart(), booking.getStart());
        assertEquals(bookingDtoRequest.getEnd(), booking.getEnd());
        assertEquals(bookingDtoRequest.getItemId(), booking.getItem().getId());
        assertEquals(status, booking.getStatus());
        assertEquals(userId, booking.getBooker().getId());
    }
}
