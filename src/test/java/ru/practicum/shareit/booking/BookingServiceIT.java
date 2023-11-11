package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisplayName("BookingService должен (IT)")
public class BookingServiceIT {
    private final BookingService bookingService;
    private final EntityManager em;

    private static final Integer from = 0;
    private static final Integer size = 20;

    @DisplayName("создавать бронирование")
    @Test
    @Rollback(value = false)
    void shouldCreateBooking() {
        Long bookerId = 2L;
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        BookingDtoResponse result = bookingService.createBooking(bookerId, bookingDtoRequest);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query
                .setParameter("id", result.getId())
                .getSingleResult();

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
    }

    @DisplayName("подтверждать бронирование")
    @Test
    void shouldApproveBooking() {
        Long ownerId = 2L;
        Long bookingId = 1L;

        BookingDtoResponse result = bookingService.approveBooking(ownerId, bookingId, true);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query
                .setParameter("id", result.getId())
                .getSingleResult();

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
    }

    @DisplayName("возвращать бронирование по id")
    @Test
    void shouldGetBookingById() {
        Long userId = 1L;
        Long bookingId = 1L;

        BookingDtoResponse result = bookingService.getBookingById(userId, bookingId);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query
                .setParameter("id", result.getId())
                .getSingleResult();

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
    }

    @DisplayName("возвращать все бронирования")
    @Test
    void shouldGetBookings() {
        Long bookerId = 1L;
        BookingState state = BookingState.ALL;

        List<BookingDtoResponse> result = bookingService.getBookings(bookerId, state, from, size);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.booker.id = :bookerId", Booking.class);
        List<Booking> bookings = query
                .setParameter("bookerId", bookerId)
                .getResultList();

        assertEquals(bookings.size(), result.size());
    }

    @DisplayName("возвращать все бронирования владельца")
    @Test
    void shouldGetOwnerBookings() {
        Long ownerId = 2L;
        BookingState state = BookingState.ALL;

        List<BookingDtoResponse> result = bookingService.getOwnerBookings(ownerId, state, from, size);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.item.owner.id = :ownerId", Booking.class);
        List<Booking> bookings = query
                .setParameter("ownerId", ownerId)
                .getResultList();

        assertEquals(bookings.size(), result.size());
    }
}
