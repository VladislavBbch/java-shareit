package ru.practicum.shareIt.server.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareIt.server.PageRequestByElement;
import ru.practicum.shareIt.server.booking.dto.BookingDtoRequest;
import ru.practicum.shareIt.server.booking.dto.BookingDtoResponse;
import ru.practicum.shareIt.server.booking.dto.BookingMapper;
import ru.practicum.shareIt.server.booking.model.Booking;
import ru.practicum.shareIt.server.booking.model.BookingState;
import ru.practicum.shareIt.server.booking.model.BookingStatus;
import ru.practicum.shareIt.server.exception.ObjectNotFoundException;
import ru.practicum.shareIt.server.item.ItemRepository;
import ru.practicum.shareIt.server.item.model.Item;
import ru.practicum.shareIt.server.user.UserRepository;
import ru.practicum.shareIt.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService должен")
public class BookingServiceTest {
    @InjectMocks
    BookingService bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    BookingMapper bookingMapper;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;

    private static final Long userId = 1L;
    private static final Integer from = 0;
    private static final Integer size = 1;
    private static final Sort sort = Sort.by(Sort.Direction.DESC, "start");
    private static final PageRequest pageRequest = PageRequestByElement.of(from, size, sort);

    @DisplayName("создавать бронирование")
    @Test
    void createBooking_whenBookingValid_thenCreateBooking() {
        User booker = new User(userId, "booker@mail.ru", "Бронирующий");
        Long itemId = 1L;
        Long ownerId = 2L;
        Item item = new Item(itemId, "Название", "Описание", true, new User(ownerId, "owner@mail.ru", "Владелец"), null);
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingStatus status = BookingStatus.WAITING;
        Booking booking = new Booking(bookingId, start, end, status, booker, item);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder().build();
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingMapper.toBooking(bookingDtoRequest, userId, status)).thenReturn(booking);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.isItemBookingNotAvailableForDates(itemId, start, end)).thenReturn(false);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingDto(booking, item)).thenReturn(bookingDtoResponse);

        BookingDtoResponse result = bookingService.createBooking(userId, bookingDtoRequest);

        assertEquals(result, bookingDtoResponse);
        verify(bookingRepository, times(1)).save(booking);
    }

    @DisplayName("ObjectNotFoundException при попытке создания бронирования на собственную вещь")
    @Test
    void createBooking_whenBookOwnItem_thenObjectNotFoundException() {
        User booker = new User(userId, "booker@mail.ru", "Бронирующий");
        Long itemId = 1L;
        Item item = new Item(itemId, "Название", "Описание", true, new User(userId, "owner@mail.ru", "Владелец"), null);
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingStatus status = BookingStatus.WAITING;
        Booking booking = new Booking(bookingId, start, end, status, booker, item);
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingMapper.toBooking(bookingDtoRequest, userId, status)).thenReturn(booking);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ObjectNotFoundException.class, () -> bookingService.createBooking(userId, bookingDtoRequest));
    }

    @DisplayName("подтверждать бронирование")
    @Test
    void approveBooking_whenApproved_thenApproveBooking() {
        Item item = new Item(1L, "Название", "Описание", true, new User(userId, "owner@mail.ru", "Владелец"), null);
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingStatus status = BookingStatus.WAITING;
        Booking booking = new Booking(bookingId, start, end, status, new User(), item);
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDtoResponse);

        BookingDtoResponse result = bookingService.approveBooking(userId, bookingId, true);

        assertEquals(result, bookingDtoResponse);
        assertEquals(booking.getStatus(), BookingStatus.APPROVED);
        verify(bookingRepository, times(1)).save(booking);
    }

    @DisplayName("отказывать в бронировании")
    @Test
    void approveBooking_whenRejected_thenRejectBooking() {
        Item item = new Item(1L, "Название", "Описание", true, new User(userId, "owner@mail.ru", "Владелец"), null);
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingStatus status = BookingStatus.WAITING;
        Booking booking = new Booking(bookingId, start, end, status, new User(), item);
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDtoResponse);

        BookingDtoResponse result = bookingService.approveBooking(userId, bookingId, false);

        assertEquals(result, bookingDtoResponse);
        assertEquals(booking.getStatus(), BookingStatus.REJECTED);
        verify(bookingRepository, times(1)).save(booking);
    }

    @DisplayName("возвращать бронирование по id")
    @Test
    void getBookingById_whenBookingExistAndOwnerOrBooker_thenReturnBooking() {
        User booker = new User(userId, "booker@mail.ru", "Бронирующий");
        Long ownerId = 2L;
        Item item = new Item(1L, "Название", "Описание", true, new User(ownerId, "owner@mail.ru", "Владелец"), null);
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingStatus status = BookingStatus.WAITING;
        Booking booking = new Booking(bookingId, start, end, status, booker, item);
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDtoResponse);

        BookingDtoResponse result = bookingService.getBookingById(userId, bookingId);

        assertEquals(result, bookingDtoResponse);
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @DisplayName("возвращать все бронирования")
    @Test
    void getBookings_whenAllBookings_thenReturnBookings() {
        BookingState state = BookingState.ALL;
        List<Booking> bookings = List.of();
        List<BookingDtoResponse> bookingDtos = List.of();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBookerId(userId, pageRequest)).thenReturn(bookings);
        when(bookingMapper.toBookingDto(bookings)).thenReturn(bookingDtos);

        List<BookingDtoResponse> result = bookingService.getBookings(userId, state, from, size);

        assertEquals(result, bookingDtos);
        verify(bookingRepository, times(1)).findAllByBookerId(userId, pageRequest);
    }

    @DisplayName("возвращать бронирования ожидающие подтверждения")
    @Test
    void getBookings_whenWaitingBookings_thenReturnBookings() {
        BookingState state = BookingState.WAITING;
        BookingStatus status = BookingStatus.WAITING;
        List<Booking> bookings = List.of();
        List<BookingDtoResponse> bookingDtos = List.of();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBookerIdAndStatus(userId, status, pageRequest)).thenReturn(bookings);
        when(bookingMapper.toBookingDto(bookings)).thenReturn(bookingDtos);

        List<BookingDtoResponse> result = bookingService.getBookings(userId, state, from, size);

        assertEquals(result, bookingDtos);
        verify(bookingRepository, times(1)).findAllByBookerIdAndStatus(userId, status, pageRequest);
    }

    @DisplayName("возвращать отклоненные бронирования")
    @Test
    void getBookings_whenRejectedBookings_thenReturnBookings() {
        BookingState state = BookingState.REJECTED;
        BookingStatus status = BookingStatus.REJECTED;
        List<Booking> bookings = List.of();
        List<BookingDtoResponse> bookingDtos = List.of();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBookerIdAndStatus(userId, status, pageRequest)).thenReturn(bookings);
        when(bookingMapper.toBookingDto(bookings)).thenReturn(bookingDtos);

        List<BookingDtoResponse> result = bookingService.getBookings(userId, state, from, size);

        assertEquals(result, bookingDtos);
        verify(bookingRepository, times(1)).findAllByBookerIdAndStatus(userId, status, pageRequest);
    }

    @DisplayName("возвращать текущие бронирования")
    @Test
    void getBookings_whenCurrentBookings_thenReturnBookings() {
        BookingState state = BookingState.CURRENT;
        List<Booking> bookings = List.of();
        List<BookingDtoResponse> bookingDtos = List.of();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any())).thenReturn(bookings);
        when(bookingMapper.toBookingDto(bookings)).thenReturn(bookingDtos);

        List<BookingDtoResponse> result = bookingService.getBookings(userId, state, from, size);

        assertEquals(result, bookingDtos);
        verify(bookingRepository, times(1)).findAllByBookerIdAndStartBeforeAndEndAfter(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any());
    }

    @DisplayName("возвращать будущие бронирования")
    @Test
    void getBookings_whenFutureBookings_thenReturnBookings() {
        BookingState state = BookingState.FUTURE;
        List<Booking> bookings = List.of();
        List<BookingDtoResponse> bookingDtos = List.of();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBookerIdAndStartAfter(
                anyLong(), any(LocalDateTime.class), any())).thenReturn(bookings);
        when(bookingMapper.toBookingDto(bookings)).thenReturn(bookingDtos);

        List<BookingDtoResponse> result = bookingService.getBookings(userId, state, from, size);

        assertEquals(result, bookingDtos);
        verify(bookingRepository, times(1)).findAllByBookerIdAndStartAfter(
                anyLong(), any(LocalDateTime.class), any());
    }

    @DisplayName("возвращать прошедшие бронирования")
    @Test
    void getBookings_whenPastBookings_thenReturnBookings() {
        BookingState state = BookingState.PAST;
        List<Booking> bookings = List.of();
        List<BookingDtoResponse> bookingDtos = List.of();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBookerIdAndEndBefore(
                anyLong(), any(LocalDateTime.class), any())).thenReturn(bookings);
        when(bookingMapper.toBookingDto(bookings)).thenReturn(bookingDtos);

        List<BookingDtoResponse> result = bookingService.getBookings(userId, state, from, size);

        assertEquals(result, bookingDtos);
        verify(bookingRepository, times(1)).findAllByBookerIdAndEndBefore(
                anyLong(), any(LocalDateTime.class), any());
    }

    @DisplayName("возвращать все бронирования владельца")
    @Test
    void getBookings_whenAllOwnerBookings_thenReturnBookings() {
        BookingState state = BookingState.ALL;
        List<Booking> bookings = List.of();
        List<BookingDtoResponse> bookingDtos = List.of();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByItemOwnerId(userId, pageRequest)).thenReturn(bookings);
        when(bookingMapper.toBookingDto(bookings)).thenReturn(bookingDtos);

        List<BookingDtoResponse> result = bookingService.getOwnerBookings(userId, state, from, size);

        assertEquals(result, bookingDtos);
        verify(bookingRepository, times(1)).findAllByItemOwnerId(userId, pageRequest);
    }

    @DisplayName("возвращать бронирования владельца ожидающие подтверждения")
    @Test
    void getBookings_whenOwnerWaitingBookings_thenReturnBookings() {
        BookingState state = BookingState.WAITING;
        BookingStatus status = BookingStatus.WAITING;
        List<Booking> bookings = List.of();
        List<BookingDtoResponse> bookingDtos = List.of();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByItemOwnerIdAndStatus(userId, status, pageRequest)).thenReturn(bookings);
        when(bookingMapper.toBookingDto(bookings)).thenReturn(bookingDtos);

        List<BookingDtoResponse> result = bookingService.getOwnerBookings(userId, state, from, size);

        assertEquals(result, bookingDtos);
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatus(userId, status, pageRequest);
    }

    @DisplayName("возвращать отклоненные бронирования владельца")
    @Test
    void getBookings_whenOwnerRejectedBookings_thenReturnBookings() {
        BookingState state = BookingState.REJECTED;
        BookingStatus status = BookingStatus.REJECTED;
        List<Booking> bookings = List.of();
        List<BookingDtoResponse> bookingDtos = List.of();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByItemOwnerIdAndStatus(userId, status, pageRequest)).thenReturn(bookings);
        when(bookingMapper.toBookingDto(bookings)).thenReturn(bookingDtos);

        List<BookingDtoResponse> result = bookingService.getOwnerBookings(userId, state, from, size);

        assertEquals(result, bookingDtos);
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatus(userId, status, pageRequest);
    }

    @DisplayName("возвращать текущие бронирования владельца")
    @Test
    void getBookings_whenOwnerCurrentBookings_thenReturnBookings() {
        BookingState state = BookingState.CURRENT;
        List<Booking> bookings = List.of();
        List<BookingDtoResponse> bookingDtos = List.of();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any())).thenReturn(bookings);
        when(bookingMapper.toBookingDto(bookings)).thenReturn(bookingDtos);

        List<BookingDtoResponse> result = bookingService.getOwnerBookings(userId, state, from, size);

        assertEquals(result, bookingDtos);
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStartBeforeAndEndAfter(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any());
    }

    @DisplayName("возвращать будущие бронирования владельца")
    @Test
    void getBookings_whenOwnerFutureBookings_thenReturnBookings() {
        BookingState state = BookingState.FUTURE;
        List<Booking> bookings = List.of();
        List<BookingDtoResponse> bookingDtos = List.of();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByItemOwnerIdAndStartAfter(
                anyLong(), any(LocalDateTime.class), any())).thenReturn(bookings);
        when(bookingMapper.toBookingDto(bookings)).thenReturn(bookingDtos);

        List<BookingDtoResponse> result = bookingService.getOwnerBookings(userId, state, from, size);

        assertEquals(result, bookingDtos);
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStartAfter(
                anyLong(), any(LocalDateTime.class), any());
    }

    @DisplayName("возвращать прошедшие бронирования владельца")
    @Test
    void getBookings_whenOwnerPastBookings_thenReturnBookings() {
        BookingState state = BookingState.PAST;
        List<Booking> bookings = List.of();
        List<BookingDtoResponse> bookingDtos = List.of();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByItemOwnerIdAndEndBefore(
                anyLong(), any(LocalDateTime.class), any())).thenReturn(bookings);
        when(bookingMapper.toBookingDto(bookings)).thenReturn(bookingDtos);

        List<BookingDtoResponse> result = bookingService.getOwnerBookings(userId, state, from, size);

        assertEquals(result, bookingDtos);
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndEndBefore(
                anyLong(), any(LocalDateTime.class), any());
    }
}
