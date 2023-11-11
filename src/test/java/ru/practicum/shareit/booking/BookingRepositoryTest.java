package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DisplayName("BookingRepository должен")
public class BookingRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    BookingRepository bookingRepository;

    User user1, user2;
    Item item1, item2;
    Booking booking1, booking2, booking3, booking4, booking5;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(null, "email1@mail.ru", "Имя1"));
        user2 = userRepository.save(new User(null, "email2@mail.ru", "Имя2"));
        item1 = itemRepository.save(new Item(null, "Название1", "Описание1", true, user1, null));
        item2 = itemRepository.save(new Item(null, "Название2", "Описание2", false, user2, null));
        LocalDateTime now = LocalDateTime.now();
        booking1 = bookingRepository.save(new Booking(null, now.minusDays(1), now.plusDays(1), BookingStatus.APPROVED, user2, item1));
        booking2 = bookingRepository.save(new Booking(null, now.plusDays(1).plusHours(1), now.plusDays(2), BookingStatus.REJECTED, user2, item1));
        booking3 = bookingRepository.save(new Booking(null, now.plusDays(2).plusHours(1), now.plusDays(3), BookingStatus.WAITING, user2, item1));
        booking4 = bookingRepository.save(new Booking(null, now.minusDays(2), now.minusDays(1), BookingStatus.APPROVED, user1, item2));
        booking5 = bookingRepository.save(new Booking(null, now.plusDays(1).plusHours(1), now.plusDays(2), BookingStatus.APPROVED, user1, item2));
    }

    @AfterEach
    void afterEach() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("возвращать все бронирования по id забронировавшего")
    @Test
    void shouldFindAllByBookerId() {
        List<Booking> bookings = bookingRepository.findAllByBookerId(user2.getId(), null);
        assertEquals(3, bookings.size());
    }

    @DisplayName("возвращать все бронирования по id забронировавшего и статусу бронирования")
    @Test
    void shouldFindAllByBookerIdAndStatus() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatus(
                user2.getId(), BookingStatus.APPROVED, null);
        assertEquals(1, bookings.size());
    }

    @DisplayName("возвращать все текущие бронирования по id забронировавшего")
    @Test
    void shouldFindAllByBookerIdAndStartBeforeAndEndAfter() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(
                user2.getId(), LocalDateTime.now(), LocalDateTime.now(), null);
        assertEquals(1, bookings.size());
    }

    @DisplayName("возвращать все будущие бронирования по id забронировавшего")
    @Test
    void shouldFindAllByBookerIdAndStartAfter() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartAfter(
                user1.getId(), LocalDateTime.now(), null);
        assertEquals(1, bookings.size());
    }

    @DisplayName("возвращать все прошедшие бронирования по id забронировавшего")
    @Test
    void shouldFindAllByBookerIdAndEndBefore() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndBefore(
                user1.getId(), LocalDateTime.now(), null);
        assertEquals(1, bookings.size());
    }

    @DisplayName("возвращать все бронирования по id владельца")
    @Test
    void shouldFindAllByItemOwnerId() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(user1.getId(), null);
        assertEquals(3, bookings.size());
    }

    @DisplayName("возвращать все бронирования по id владельца и статусу бронирования")
    @Test
    void shouldFindAllByItemOwnerIdAndStatus() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatus(
                user1.getId(), BookingStatus.REJECTED, null);
        assertEquals(1, bookings.size());
    }

    @DisplayName("возвращать все текущие бронирования по id владельца")
    @Test
    void shouldFindAllByItemOwnerIdAndStartBeforeAndEndAfter() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(
                user1.getId(), LocalDateTime.now(), LocalDateTime.now(), null);
        assertEquals(1, bookings.size());
    }

    @DisplayName("возвращать все будущие бронирования по id владельца")
    @Test
    void shouldFindAllByItemOwnerIdAndStartAfter() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartAfter(
                user2.getId(), LocalDateTime.now(), null);
        assertEquals(1, bookings.size());
    }

    @DisplayName("возвращать все прошедшие бронирования по id владельца")
    @Test
    void shouldFindAllByItemOwnerIdAndEndBefore() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndEndBefore(
                user2.getId(), LocalDateTime.now(), null);
        assertEquals(1, bookings.size());
    }

    @DisplayName("проверять недоступно ли бронирование вещи для дат")
    @Test
    void shouldCheckIsItemBookingNotAvailableForDates() {
        Boolean isNotAvailable = bookingRepository.isItemBookingNotAvailableForDates(
                item1.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        assertTrue(isNotAvailable);
    }

    @DisplayName("проверять брал ли пользователь вещь в аренду")
    @Test
    void shouldCheckExistsAllByBookerIdAndItemIdAndStatusAndEndBefore() {
        Boolean isBooked = bookingRepository.existsAllByBookerIdAndItemIdAndStatusAndEndBefore(
                user1.getId(), item2.getId(), BookingStatus.APPROVED, LocalDateTime.now());
        assertTrue(isBooked);
    }

    @DisplayName("возвращать все бронирования по id вещи и статусу бронирования")
    @Test
    void shouldFindAllByItemIdAndStatus() {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStatus(
                item1.getId(), BookingStatus.WAITING, null);
        assertEquals(1, bookings.size());
    }

    @DisplayName("возвращать все бронирования по списку id вещей и статусу бронирования")
    @Test
    void shouldFindAllByItemIdInAndStatus() {
        List<Booking> bookings = bookingRepository.findAllByItemIdInAndStatus(
                List.of(item1.getId(), item2.getId()), BookingStatus.APPROVED, null);
        assertEquals(3, bookings.size());
    }
}
