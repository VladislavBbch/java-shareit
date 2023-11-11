package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.PageRequestByElement;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemService должен")
public class ItemServiceTest {
    @InjectMocks
    ItemService itemService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemMapper itemMapper;
    @Mock
    CommentMapper commentMapper;
    @Mock
    EntityManager entityManager;

    private static final Long userId = 1L;
    private static final Integer from = 0;
    private static final Integer size = 1;
    private static final PageRequest pageRequest = PageRequestByElement.of(from, size);

    @DisplayName("создавать вещь")
    @Test
    void createItem_whenItemValid_thenCreateItem() {
        Item item = new Item();
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder().build();
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItem(itemDtoRequest, userId)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDtoResponse);

        ItemDtoResponse result = itemService.createItem(userId, itemDtoRequest);

        assertEquals(result, itemDtoResponse);
        verify(itemRepository, times(1)).save(item);
    }

    @DisplayName("возвращать вещи")
    @Test
    void getItems_whenItemsExist_thenReturnItems() {
        Item item = new Item();
        List<Item> items = List.of(item);
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder().build();
        List<ItemDtoResponse> itemDtoResponses = List.of(itemDtoResponse);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findAllByOwnerIdOrderById(userId, pageRequest)).thenReturn(items);
        when(itemMapper.toItemDto(items, Map.of(), Map.of())).thenReturn(itemDtoResponses);

        List<ItemDtoResponse> result = itemService.getItems(userId, from, size);

        assertEquals(result, itemDtoResponses);
        verify(itemRepository, times(1)).findAllByOwnerIdOrderById(anyLong(), any());
        verify(bookingRepository, times(1)).findAllByItemIdInAndStatus(any(), any(), any());
        verify(commentRepository, times(1)).findAllByItemIdIn(any());
    }

    @DisplayName("возвращать пустой список вещей")
    @Test
    void getItems_whenItemsNotExist_thenReturnEmpty() {
        List<ItemDtoResponse> itemDtoResponses = List.of();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findAllByOwnerIdOrderById(userId, pageRequest)).thenReturn(List.of());

        List<ItemDtoResponse> result = itemService.getItems(userId, from, size);

        assertEquals(result, itemDtoResponses);
        verify(itemRepository, times(1)).findAllByOwnerIdOrderById(anyLong(), any());
        verify(bookingRepository, never()).findAllByItemIdInAndStatus(any(), any(), any());
        verify(commentRepository, never()).findAllByItemIdIn(any());
    }

    @DisplayName("возвращать вещь по id")
    @Test
    void getItemById_whenItemExistAndOwner_thenReturnItem() {
        Long itemId = 1L;
        Item item = new Item(1L, "Имя", "Описание", true, new User(userId, "email@mail.ru", "Имя"), null);
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toItemDto(item, List.of(), List.of())).thenReturn(itemDtoResponse);

        ItemDtoResponse result = itemService.getItemById(userId, itemId);

        assertEquals(result, itemDtoResponse);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).findAllByItemIdAndStatus(anyLong(), any(), any());
        verify(commentRepository, times(1)).findAllByItemId(itemId);
    }

    @DisplayName("ObjectNotFoundException при возвращении не существующей вещи по id")
    @Test
    void getItemById_whenItemNotExist_thenObjectNotFoundException() {
        Long itemId = 1000L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenThrow(new ObjectNotFoundException("Несуществующий id вещи: " + itemId));

        assertThrows(ObjectNotFoundException.class, () -> itemService.getItemById(userId, itemId));
        verify(itemRepository, times(1)).findById(itemId);
    }

    @DisplayName("обновлять вещь")
    @Test
    void updateItem_whenItemExistAndOwner_thenUpdateItem() {
        Long itemId = 1L;
        Item item = new Item(1L, "Имя", "Описание", true, new User(userId, "email@mail.ru", "Имя"), null);
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder().build();
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toItem(itemDtoRequest, userId, itemId)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item, List.of(), List.of())).thenReturn(itemDtoResponse);

        ItemDtoResponse result = itemService.updateItem(userId, itemId, itemDtoRequest);

        assertEquals(result, itemDtoResponse);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).findAllByItemIdAndStatus(anyLong(), any(), any());
        verify(itemRepository, times(1)).save(item);
    }

    @DisplayName("удалять вещь")
    @Test
    void deleteItem_whenItemExistAndOwner_thenDeleteItem() {
        Long itemId = 1L;
        Item item = new Item();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        doNothing().when(itemRepository).deleteByIdAndOwnerId(itemId, userId);

        itemService.deleteItem(userId, itemId);

        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(1)).deleteByIdAndOwnerId(itemId, userId);
    }

    @DisplayName("искать вещи")
    @Test
    void searchItems_whenTextNotBlank_thenSearchItems() {
        String text = "текст";
        String resultText = "%текст%";
        List<Item> items = List.of();
        List<ItemDtoResponse> itemDtoResponses = List.of();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findAllByIsAvailableAndTextInNameOrDescription(resultText, pageRequest)).thenReturn(items);
        when(itemMapper.toItemDto(items)).thenReturn(itemDtoResponses);

        List<ItemDtoResponse> result = itemService.searchItems(userId, text, from, size);

        assertEquals(result, itemDtoResponses);
        verify(itemRepository, times(1)).findAllByIsAvailableAndTextInNameOrDescription(resultText, pageRequest);
    }

    @DisplayName("создавать комментарий")
    @Test
    void createComment_whenItemValid_thenCreateItem() {
        Long itemId = 1L;
        Item item = new Item();
        Comment comment = new Comment();
        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder().build();
        CommentDtoResponse commentDtoResponse = CommentDtoResponse.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsAllByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any())).thenReturn(true);
        when(commentMapper.toComment(userId, itemId, commentDtoRequest)).thenReturn(comment);
        when(commentRepository.saveAndFlush(comment)).thenReturn(comment);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDtoResponse);

        CommentDtoResponse result = itemService.createComment(userId, itemId, commentDtoRequest);

        assertEquals(result, commentDtoResponse);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).existsAllByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any());
        verify(commentRepository, times(1)).saveAndFlush(comment);
    }

    @DisplayName("ValidateException при попытке создать кооментарий к вещи не взятой в аренду")
    @Test
    void createComment_whenItemNotBooked_thenValidateException() {
        Long itemId = 1L;
        Item item = new Item();
        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsAllByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any())).thenReturn(false);

        assertThrows(ValidateException.class, () -> itemService.createComment(userId, itemId, commentDtoRequest));
        verify(itemRepository, times(1)).findById(itemId);
    }
}
