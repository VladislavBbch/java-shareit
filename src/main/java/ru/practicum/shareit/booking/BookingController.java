package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constant;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse createBooking(@RequestHeader(Constant.HEADER_USER_ID) Long bookerId,
                                            @RequestBody @Valid BookingDtoRequest booking) {
        log.info("Начало обработки запроса на создание бронирования: {}", booking);
        BookingDtoResponse newBooking = bookingService.createBooking(bookerId, booking);
        log.info("Окончание обработки запроса на создание бронирования");
        return newBooking;
    }

    @PatchMapping("/{id}")
    public BookingDtoResponse approveBooking(@RequestHeader(Constant.HEADER_USER_ID) Long ownerId,
                                             @PathVariable Long id,
                                             @RequestParam("approved") Boolean isApproved) {
        log.info("Начало обработки запроса на подтверждение бронирования: {}", id);
        BookingDtoResponse existingBooking = bookingService.approveBooking(ownerId, id, isApproved);
        log.info("Окончание обработки запроса на подтверждение бронирования");
        return existingBooking;
    }

    @GetMapping
    public List<BookingDtoResponse> getBookings(@RequestHeader(Constant.HEADER_USER_ID) Long bookerId,
                                                @RequestParam(defaultValue = "ALL") BookingState state,
                                                @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                @RequestParam(defaultValue = "10") @Min(1) @Max(30) Integer size) {
        return bookingService.getBookings(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getOwnerBookings(@RequestHeader(Constant.HEADER_USER_ID) Long ownerId,
                                                     @RequestParam(defaultValue = "ALL") BookingState state,
                                                     @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                     @RequestParam(defaultValue = "10") @Min(1) @Max(30) Integer size) {
        return bookingService.getOwnerBookings(ownerId, state, from, size);
    }

    @GetMapping("/{id}")
    public BookingDtoResponse getBookingById(@RequestHeader(Constant.HEADER_USER_ID) Long userId,
                                             @PathVariable Long id) {
        log.info("Начало обработки запроса по получению бронирования: {}", id);
        BookingDtoResponse booking = bookingService.getBookingById(userId, id);
        log.info("Окончание обработки запроса по получению бронирования");
        return booking;
    }
}
