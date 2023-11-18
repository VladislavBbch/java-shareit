package ru.practicum.shareIt.server.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareIt.server.Constant;
import ru.practicum.shareIt.server.booking.dto.BookingDtoRequest;
import ru.practicum.shareIt.server.booking.dto.BookingDtoResponse;
import ru.practicum.shareIt.server.booking.model.BookingState;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse createBooking(@RequestHeader(Constant.HEADER_USER_ID) Long bookerId,
                                            @RequestBody BookingDtoRequest booking) {
        log.info("Начало обработки запроса на создание бронирования: {}", booking);
        BookingDtoResponse newBooking = bookingService.createBooking(bookerId, booking);
        log.info("Окончание обработки запроса на создание бронирования");
        return newBooking;
    }

    @PatchMapping("/{id}")
    public BookingDtoResponse approveBooking(@RequestHeader(Constant.HEADER_USER_ID) Long ownerId,
                                             @PathVariable Long id,
                                             @RequestParam Boolean isApproved) {
        log.info("Начало обработки запроса на подтверждение бронирования: {}", id);
        BookingDtoResponse existingBooking = bookingService.approveBooking(ownerId, id, isApproved);
        log.info("Окончание обработки запроса на подтверждение бронирования");
        return existingBooking;
    }

    @GetMapping
    public List<BookingDtoResponse> getBookings(@RequestHeader(Constant.HEADER_USER_ID) Long bookerId,
                                                @RequestParam BookingState state,
                                                @RequestParam Integer from,
                                                @RequestParam Integer size) {
        return bookingService.getBookings(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getOwnerBookings(@RequestHeader(Constant.HEADER_USER_ID) Long ownerId,
                                                     @RequestParam BookingState state,
                                                     @RequestParam Integer from,
                                                     @RequestParam Integer size) {
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
