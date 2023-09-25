package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final EntityManager entityManager;

    public ItemDtoResponse createItem(Long userId, ItemDtoRequest itemDto) {
        checkUser(userId);
        Item item = itemMapper.toItem(itemDto, userId);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    public List<ItemDtoResponse> getItems(Long userId) {
        checkUser(userId);
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(userId);
        if (items.size() > 0) {
            List<Long> ids = items.stream().map(Item::getId).collect(toList());
            Map<Long, List<Booking>> itemsBookings = bookingRepository
                    .findAllByItemIdInAndStatus(ids, BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "start"))
                    .stream()
                    .collect(groupingBy(booking -> booking.getItem().getId(), toList()));
            Map<Long, List<Comment>> itemsComments = commentRepository.findAllByItemIdIn(ids).stream()
                    .collect(groupingBy(comment -> comment.getItem().getId(), toList()));
            return itemMapper.toItemDto(items, itemsBookings, itemsComments);
        }
        return itemMapper.toItemDto(items);
    }

    public ItemDtoResponse getItemById(Long userId, Long itemId) {
        checkUser(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id вещи: " + itemId));
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        if (userId.equals(item.getOwner().getId())) {
            List<Booking> bookings = bookingRepository
                    .findAllByItemIdAndStatus(itemId, BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "start"));
            return itemMapper.toItemDto(item, bookings, comments);
        }
        return itemMapper.toItemDto(item, comments);
    }

    public ItemDtoResponse updateItem(Long userId, Long id, ItemDtoRequest itemDto) {
        checkUser(userId);
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id вещи: " + id));
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("Несуществующий id вещи: " + id);
        }
        Item item = itemMapper.toItem(itemDto, userId, id);

        String name = item.getName();
        if (name != null && !name.isBlank()) {
            existingItem.setName(name);
        }
        String description = item.getDescription();
        if (description != null && !description.isBlank()) {
            existingItem.setDescription(description);
        }
        Boolean isAvailable = item.getIsAvailable();
        if (isAvailable != null) {
            existingItem.setIsAvailable(isAvailable);
        }
        List<Booking> bookings = bookingRepository
                .findAllByItemIdAndStatus(id, BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "start"));
        return itemMapper.toItemDto(itemRepository.save(existingItem), bookings, List.of());
    }

    public void deleteItem(Long userId, Long itemId) {
        checkUser(userId);
        itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id вещи: " + itemId));
        itemRepository.deleteByIdAndOwnerId(itemId, userId);
    }

    public List<ItemDtoResponse> searchItems(Long userId, String text) {
        if (text.isBlank()) {
            return List.of();
        }
        checkUser(userId);
        text = "%" + text + "%";
        return itemMapper.toItemDto(itemRepository.findAllByIsAvailableAndTextInNameOrDescription(text));
    }

    @Transactional
    public CommentDtoResponse createComment(Long userId, Long itemId, CommentDtoRequest commentDto) {
        checkUser(userId);
        itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id вещи: " + itemId));
        if (!bookingRepository.existsAllByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now())) {
            throw new ValidateException("Оставить комментарий можно только для вещи взятой в аренду");
        }
        Comment comment = commentMapper.toComment(userId, itemId, commentDto);
        comment = commentRepository.saveAndFlush(comment);
        entityManager.refresh(comment);
        return commentMapper.toCommentDto(comment);
    }

    private void checkUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id пользователя: " + userId));
    }
}
