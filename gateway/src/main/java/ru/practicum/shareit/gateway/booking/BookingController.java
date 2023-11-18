package ru.practicum.shareit.gateway.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.Constant;
import ru.practicum.shareit.gateway.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.gateway.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(Constant.HEADER_USER_ID) Long bookerId,
                                                @RequestBody @Valid BookingDtoRequest booking) {
        log.info("Начало обработки запроса на создание бронирования: {}", booking);
        ResponseEntity<Object> newBooking = bookingClient.createBooking(bookerId, booking);
        log.info("Окончание обработки запроса на создание бронирования");
        return newBooking;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(Constant.HEADER_USER_ID) Long ownerId,
                                                 @PathVariable Long id,
                                                 @RequestParam("approved") Boolean isApproved) {
        log.info("Начало обработки запроса на подтверждение бронирования: {}", id);
        ResponseEntity<Object> existingBooking = bookingClient.approveBooking(ownerId, id, isApproved);
        log.info("Окончание обработки запроса на подтверждение бронирования");
        return existingBooking;
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(Constant.HEADER_USER_ID) Long bookerId,
                                              @RequestParam(defaultValue = "ALL") BookingState state,
                                              @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                              @RequestParam(defaultValue = "10") @Min(1) @Max(30) Integer size) {
        log.info("Начало обработки запроса на получение всех бронирований как бронирующего");
        ResponseEntity<Object> bookings = bookingClient.getBookings(bookerId, state, from, size);
        log.info("Окончание обработки запроса на получение всех бронирований как бронирующего");
        return bookings;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader(Constant.HEADER_USER_ID) Long ownerId,
                                                   @RequestParam(defaultValue = "ALL") BookingState state,
                                                   @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                   @RequestParam(defaultValue = "10") @Min(1) @Max(30) Integer size) {
        log.info("Начало обработки запроса на получение всех бронирований владельца");
        ResponseEntity<Object> bookings = bookingClient.getOwnerBookings(ownerId, state, from, size);
        log.info("Окончание обработки запроса на получение всех бронирований владельца");
        return bookings;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(Constant.HEADER_USER_ID) Long userId,
                                                 @PathVariable Long id) {
        log.info("Начало обработки запроса по получению бронирования: {}", id);
        ResponseEntity<Object> booking = bookingClient.getBooking(userId, id);
        log.info("Окончание обработки запроса по получению бронирования");
        return booking;
    }
}
