package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final CommentMapper commentMapper;

    public Item toItem(ItemDtoRequest itemDto, Long ownerId) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .isAvailable(itemDto.getIsAvailable())
                .owner(User.builder().id(ownerId).build())
                .build();
    }

    public Item toItem(ItemDtoRequest itemDto, Long ownerId, Long id) {
        return Item.builder()
                .id(id)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .isAvailable(itemDto.getIsAvailable())
                .owner(User.builder().id(ownerId).build())
                .build();
    }

    public ItemDtoResponse toItemDto(Item item) {
        return ItemDtoResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .isAvailable(item.getIsAvailable())
                .build();
    }

    public ItemDtoResponse toItemDto(Item item, List<Comment> comments) {
        return ItemDtoResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .isAvailable(item.getIsAvailable())
                .comments(comments.size() > 0 ? commentMapper.toCommentDto(comments) : List.of())
                .build();
    }

    public ItemDtoResponse toItemDto(Item item, List<Booking> bookings, List<Comment> comments) {
        ItemDtoResponse.Booking lastBooking = null;
        ItemDtoResponse.Booking nextBooking = null;

        if (bookings.size() > 0) {
            LocalDateTime now = LocalDateTime.now();
            if (bookings.size() == 1) {
                Booking booking = bookings.get(0);
                if (booking.getStart().isBefore(now)) {
                    lastBooking = new ItemDtoResponse.Booking(booking.getId(), booking.getBooker().getId());
                } else {
                    nextBooking = new ItemDtoResponse.Booking(booking.getId(), booking.getBooker().getId());
                }
            } else {
                for (int i = 0; i < bookings.size(); i++) {
                    if (i + 1 < bookings.size()) {
                        if (bookings.get(i).getStart().isBefore(now) && bookings.get(i + 1).getStart().isAfter(now)) {
                            lastBooking = new ItemDtoResponse.Booking(bookings.get(i).getId(),
                                    bookings.get(i).getBooker().getId());
                            nextBooking = new ItemDtoResponse.Booking(bookings.get(i + 1).getId(),
                                    bookings.get(i + 1).getBooker().getId());
                            break;
                        }
                    } else {
                        Booking booking = bookings.get(i);
                        if (booking.getStart().isBefore(now)) {
                            lastBooking = new ItemDtoResponse.Booking(booking.getId(), booking.getBooker().getId());
                        } else {
                            nextBooking = new ItemDtoResponse.Booking(booking.getId(), booking.getBooker().getId());
                        }
                    }
                }
            }
        }
        return ItemDtoResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .isAvailable(item.getIsAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments.size() > 0 ? commentMapper.toCommentDto(comments) : List.of())
                .build();
    }

    public List<ItemDtoResponse> toItemDto(List<Item> items) {
        return items.stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());
    }

    public List<ItemDtoResponse> toItemDto(List<Item> items,
                                           Map<Long, List<Booking>> itemsBookings,
                                           Map<Long, List<Comment>> itemsComments) {
        return items.stream()
                .map(item -> toItemDto(item,
                        itemsBookings.getOrDefault(item.getId(), List.of()),
                        itemsComments.getOrDefault(item.getId(), List.of())))
                .collect(Collectors.toList());
    }
}
