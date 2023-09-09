package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    public ItemDto createItem(Long userId, ItemDto itemDto) {
        checkUser(userId);
        Item item = itemMapper.toItem(itemDto, userId);
        return itemMapper.toItemDto(itemRepository.create(item));
    }

    public List<ItemDto> getItems(Long userId) {
        checkUser(userId);
        return itemMapper.toItemDto(itemRepository.read(userId));
    }

    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.getById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id вещи: " + itemId));
        return itemMapper.toItemDto(item);
    }

    public ItemDto updateItem(Long userId, Long id, ItemDto itemDto) {
        checkUser(userId);
        getItemById(id);
        Item item = itemMapper.toItem(itemDto, userId, id);
        if (itemRepository.update(item) == 0) {
            throw new ObjectNotFoundException("Несуществующий id вещи: " + id + " для пользователя: " + userId);
        }
        return getItemById(id);
    }

    public void deleteItem(Long userId, Long itemId) {
        checkUser(userId);
        getItemById(itemId);
        itemRepository.delete(userId, itemId);
    }

    public List<ItemDto> searchItems(Long userId, String text) {
        if (text.isBlank()) {
            return List.of();
        }
        checkUser(userId);
        return itemMapper.toItemDto(itemRepository.searchItems(userId, text));
    }

    private void checkUser(Long userId) {
        userRepository.getById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id пользователя: " + userId));
    }
}
