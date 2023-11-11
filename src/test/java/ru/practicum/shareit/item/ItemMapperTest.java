package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemMapper должен")
public class ItemMapperTest {
    @InjectMocks
    ItemMapper itemMapper;
    @Mock
    CommentMapper commentMapper;

    @DisplayName("мапить сущность в dto")
    @Test
    void shouldMapItemToItemDto() {
        Long itemId = 1L;
        Long ownerId = 1L;
        Long requestId = 1L;
        Item item = new Item(itemId, "Название", "Описание", true,
                new User(ownerId, "owner@mail.ru", "Владелец"), requestId);

        List<ItemDtoResponse> itemDtoResponses = itemMapper.toItemDto(List.of(item));

        assertEquals(item.getId(), itemDtoResponses.get(0).getId());
        assertEquals(item.getName(), itemDtoResponses.get(0).getName());
        assertEquals(item.getDescription(), itemDtoResponses.get(0).getDescription());
        assertEquals(item.getIsAvailable(), itemDtoResponses.get(0).getIsAvailable());
        assertEquals(item.getRequestId(), itemDtoResponses.get(0).getRequestId());
        assertEquals(List.of(), itemDtoResponses.get(0).getComments());
        assertNull(itemDtoResponses.get(0).getLastBooking());
        assertNull(itemDtoResponses.get(0).getNextBooking());
    }

    @DisplayName("мапить сущность в dto с прошедшим бронированием")
    @Test
    void shouldMapItemToItemDtoWithLastBooking() {
        Long itemId = 1L;
        Long ownerId = 1L;
        Long requestId = 1L;
        Item item = new Item(itemId, "Название", "Описание", true,
                new User(ownerId, "owner@mail.ru", "Владелец"), requestId);
        Long userId = 2L;
        User booker = new User(userId, "booker@mail.ru", "Бронирующий");
        Booking lastBooking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(1),
                BookingStatus.APPROVED, booker, item);
        Map<Long, List<Booking>> itemsBookings = Map.of(itemId, List.of(lastBooking));
        Map<Long, List<Comment>> itemsComments = Map.of(itemId, List.of());

        List<ItemDtoResponse> itemDtoResponses = itemMapper.toItemDto(List.of(item), itemsBookings, itemsComments);

        assertEquals(item.getId(), itemDtoResponses.get(0).getId());
        assertEquals(item.getName(), itemDtoResponses.get(0).getName());
        assertEquals(item.getDescription(), itemDtoResponses.get(0).getDescription());
        assertEquals(item.getIsAvailable(), itemDtoResponses.get(0).getIsAvailable());
        assertEquals(item.getRequestId(), itemDtoResponses.get(0).getRequestId());
        assertEquals(List.of(), itemDtoResponses.get(0).getComments());
        assertEquals(lastBooking.getId(), ReflectionTestUtils.getField(itemDtoResponses.get(0).getLastBooking(), "id"));
    }

    @DisplayName("мапить сущность в dto с будущим бронированием")
    @Test
    void shouldMapItemToItemDtoWithNextBooking() {
        Long itemId = 1L;
        Long ownerId = 1L;
        Long requestId = 1L;
        Item item = new Item(itemId, "Название", "Описание", true,
                new User(ownerId, "owner@mail.ru", "Владелец"), requestId);
        Long userId = 2L;
        User booker = new User(userId, "booker@mail.ru", "Бронирующий");
        Booking nextBooking = new Booking(2L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1),
                BookingStatus.WAITING, booker, item);
        Map<Long, List<Booking>> itemsBookings = Map.of(itemId, List.of(nextBooking));
        Map<Long, List<Comment>> itemsComments = Map.of(itemId, List.of());

        List<ItemDtoResponse> itemDtoResponses = itemMapper.toItemDto(List.of(item), itemsBookings, itemsComments);

        assertEquals(item.getId(), itemDtoResponses.get(0).getId());
        assertEquals(item.getName(), itemDtoResponses.get(0).getName());
        assertEquals(item.getDescription(), itemDtoResponses.get(0).getDescription());
        assertEquals(item.getIsAvailable(), itemDtoResponses.get(0).getIsAvailable());
        assertEquals(item.getRequestId(), itemDtoResponses.get(0).getRequestId());
        assertEquals(List.of(), itemDtoResponses.get(0).getComments());
        assertEquals(nextBooking.getId(), ReflectionTestUtils.getField(itemDtoResponses.get(0).getNextBooking(), "id"));
    }

    @DisplayName("мапить сущность в dto с прошедшим и будущим бронированиями")
    @Test
    void shouldMapItemToItemDtoWithBothBookings() {
        Long itemId = 1L;
        Long ownerId = 1L;
        Long requestId = 1L;
        Item item = new Item(itemId, "Название", "Описание", true,
                new User(ownerId, "owner@mail.ru", "Владелец"), requestId);
        Long userId = 2L;
        User booker = new User(userId, "booker@mail.ru", "Бронирующий");
        Booking lastBooking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(1),
                BookingStatus.APPROVED, booker, item);
        Booking nextBooking = new Booking(2L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1),
                BookingStatus.WAITING, booker, item);
        Map<Long, List<Booking>> itemsBookings = Map.of(itemId, List.of(lastBooking, nextBooking));
        Map<Long, List<Comment>> itemsComments = Map.of(itemId, List.of());

        List<ItemDtoResponse> itemDtoResponses = itemMapper.toItemDto(List.of(item), itemsBookings, itemsComments);

        assertEquals(item.getId(), itemDtoResponses.get(0).getId());
        assertEquals(item.getName(), itemDtoResponses.get(0).getName());
        assertEquals(item.getDescription(), itemDtoResponses.get(0).getDescription());
        assertEquals(item.getIsAvailable(), itemDtoResponses.get(0).getIsAvailable());
        assertEquals(item.getRequestId(), itemDtoResponses.get(0).getRequestId());
        assertEquals(List.of(), itemDtoResponses.get(0).getComments());
        assertEquals(lastBooking.getId(), ReflectionTestUtils.getField(itemDtoResponses.get(0).getLastBooking(), "id"));
        assertEquals(nextBooking.getId(), ReflectionTestUtils.getField(itemDtoResponses.get(0).getNextBooking(), "id"));
    }

    @DisplayName("мапить сущность в dto с двумя прошедшимими бронированиями")
    @Test
    void shouldMapItemToItemDtoWithLastBookings() {
        Long itemId = 1L;
        Long ownerId = 1L;
        Long requestId = 1L;
        Item item = new Item(itemId, "Название", "Описание", true,
                new User(ownerId, "owner@mail.ru", "Владелец"), requestId);
        Long userId = 2L;
        User booker = new User(userId, "booker@mail.ru", "Бронирующий");
        Booking firstBooking = new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                BookingStatus.REJECTED, booker, item);
        Booking secondBooking = new Booking(2L, LocalDateTime.now().minusHours(12), LocalDateTime.now().minusHours(1),
                BookingStatus.APPROVED, booker, item);
        Map<Long, List<Booking>> itemsBookings = Map.of(itemId, List.of(firstBooking, secondBooking));
        Map<Long, List<Comment>> itemsComments = Map.of(itemId, List.of());

        List<ItemDtoResponse> itemDtoResponses = itemMapper.toItemDto(List.of(item), itemsBookings, itemsComments);

        assertEquals(item.getId(), itemDtoResponses.get(0).getId());
        assertEquals(item.getName(), itemDtoResponses.get(0).getName());
        assertEquals(item.getDescription(), itemDtoResponses.get(0).getDescription());
        assertEquals(item.getIsAvailable(), itemDtoResponses.get(0).getIsAvailable());
        assertEquals(item.getRequestId(), itemDtoResponses.get(0).getRequestId());
        assertEquals(List.of(), itemDtoResponses.get(0).getComments());
        assertEquals(secondBooking.getId(), ReflectionTestUtils.getField(itemDtoResponses.get(0).getLastBooking(), "id"));
    }

    @DisplayName("мапить сущность в dto с двумя будущими бронированиями")
    @Test
    void shouldMapItemToItemDtoWithNextBookings() {
        Long itemId = 1L;
        Long ownerId = 1L;
        Long requestId = 1L;
        Item item = new Item(itemId, "Название", "Описание", true,
                new User(ownerId, "owner@mail.ru", "Владелец"), requestId);
        Long userId = 2L;
        User booker = new User(userId, "booker@mail.ru", "Бронирующий");
        Booking firstBooking = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(12),
                BookingStatus.APPROVED, booker, item);
        Booking secondBooking = new Booking(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.REJECTED, booker, item);
        Map<Long, List<Booking>> itemsBookings = Map.of(itemId, List.of(firstBooking, secondBooking));
        Map<Long, List<Comment>> itemsComments = Map.of(itemId, List.of());

        List<ItemDtoResponse> itemDtoResponses = itemMapper.toItemDto(List.of(item), itemsBookings, itemsComments);

        assertEquals(item.getId(), itemDtoResponses.get(0).getId());
        assertEquals(item.getName(), itemDtoResponses.get(0).getName());
        assertEquals(item.getDescription(), itemDtoResponses.get(0).getDescription());
        assertEquals(item.getIsAvailable(), itemDtoResponses.get(0).getIsAvailable());
        assertEquals(item.getRequestId(), itemDtoResponses.get(0).getRequestId());
        assertEquals(List.of(), itemDtoResponses.get(0).getComments());
        assertEquals(firstBooking.getId(), ReflectionTestUtils.getField(itemDtoResponses.get(0).getNextBooking(), "id"));
    }

    @DisplayName("мапить dto в сущность")
    @Test
    void shouldMapItemDtoToItem() {
        Long userId = 1L;
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name("Название")
                .description("Описание")
                .isAvailable(true)
                .build();

        Item item = itemMapper.toItem(itemDtoRequest, userId);

        assertEquals(itemDtoRequest.getName(), item.getName());
        assertEquals(itemDtoRequest.getDescription(), item.getDescription());
        assertEquals(itemDtoRequest.getIsAvailable(), item.getIsAvailable());
    }
}
