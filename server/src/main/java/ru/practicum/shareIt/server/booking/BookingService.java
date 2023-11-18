package ru.practicum.shareIt.server.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareIt.server.PageRequestByElement;
import ru.practicum.shareIt.server.exception.ObjectNotFoundException;
import ru.practicum.shareIt.server.exception.ValidateException;
import ru.practicum.shareIt.server.booking.dto.BookingDtoRequest;
import ru.practicum.shareIt.server.booking.dto.BookingDtoResponse;
import ru.practicum.shareIt.server.booking.dto.BookingMapper;
import ru.practicum.shareIt.server.booking.model.Booking;
import ru.practicum.shareIt.server.booking.model.BookingState;
import ru.practicum.shareIt.server.booking.model.BookingStatus;
import ru.practicum.shareIt.server.item.ItemRepository;
import ru.practicum.shareIt.server.item.model.Item;
import ru.practicum.shareIt.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingDtoResponse createBooking(Long bookerId, BookingDtoRequest bookingDtoRequest) {
        checkUser(bookerId);
        Booking booking = bookingMapper.toBooking(bookingDtoRequest, bookerId, BookingStatus.WAITING);
        Long itemId = booking.getItem().getId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id вещи: " + itemId));
        if (Objects.equals(item.getOwner().getId(), bookerId)) {
            throw new ObjectNotFoundException("Нелья забронировать собственную вещь: " + itemId);
        }
        if (!item.getIsAvailable()) {
            throw new ValidateException("Вещь: " + itemId + " недоступна для бронирования");
        }
        if (bookingRepository.isItemBookingNotAvailableForDates(itemId,
                booking.getStart(), booking.getEnd())) {
            throw new ValidateException("Вещь: " + itemId + " недоступна для бронирования в указанные даты");
        }
        booking.setItem(item);
        return bookingMapper.toBookingDto(bookingRepository.save(booking), item);
    }

    public BookingDtoResponse approveBooking(Long ownerId, Long id, Boolean isApproved) {
        checkUser(ownerId);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id бронирования: " + id));
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ObjectNotFoundException("Несуществующий id бронирования: " + id
                    + "для подтверждения пользователем: " + ownerId);
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidateException("Бронирование уже в терминальном статусе");
        }
        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDtoResponse getBookingById(Long userId, Long id) {
        checkUser(userId);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id бронирования: " + id));
        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new ObjectNotFoundException("Несуществующий id бронирования: " + id);
        }
        return bookingMapper.toBookingDto(booking);
    }

    public List<BookingDtoResponse> getBookings(Long bookerId, BookingState state, Integer from, Integer size) {
        checkUser(bookerId);
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(bookerId,
                        PageRequestByElement.of(from, size, Sort.by(Sort.Direction.DESC, "start")));
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.WAITING,
                        PageRequestByElement.of(from, size, Sort.by(Sort.Direction.DESC, "start")));
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.REJECTED,
                        PageRequestByElement.of(from, size, Sort.by(Sort.Direction.DESC, "start")));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(bookerId, LocalDateTime.now(),
                        LocalDateTime.now(), PageRequestByElement.of(from, size, Sort.by(Sort.Direction.DESC, "start")));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(bookerId,
                        LocalDateTime.now(), PageRequestByElement.of(from, size, Sort.by(Sort.Direction.DESC, "start")));
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(bookerId,
                        LocalDateTime.now(), PageRequestByElement.of(from, size, Sort.by(Sort.Direction.DESC, "start")));
                break;
            default:
                bookings = List.of();
        }
        return bookingMapper.toBookingDto(bookings);
    }

    public List<BookingDtoResponse> getOwnerBookings(Long ownerId, BookingState state, Integer from, Integer size) {
        checkUser(ownerId);
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerId(ownerId,
                        PageRequestByElement.of(from, size, Sort.by(Sort.Direction.DESC, "start")));
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING,
                        PageRequestByElement.of(from, size, Sort.by(Sort.Direction.DESC, "start")));
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED,
                        PageRequestByElement.of(from, size, Sort.by(Sort.Direction.DESC, "start")));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(ownerId, LocalDateTime.now(),
                        LocalDateTime.now(), PageRequestByElement.of(from, size, Sort.by(Sort.Direction.DESC, "start")));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfter(ownerId,
                        LocalDateTime.now(), PageRequestByElement.of(from, size, Sort.by(Sort.Direction.DESC, "start")));
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBefore(ownerId,
                        LocalDateTime.now(), PageRequestByElement.of(from, size, Sort.by(Sort.Direction.DESC, "start")));
                break;
            default:
                bookings = List.of();
        }
        return bookingMapper.toBookingDto(bookings);
    }

    private void checkUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id пользователя: " + userId));
    }
}
