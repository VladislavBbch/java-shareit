package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.Constant;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(Constant.HEADER_USER_ID) Long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Long id) {
        log.info("Начало обработки запроса по получению вещи: {}", id);
        ItemDto item = itemService.getItemById(id);
        log.info("Окончание обработки запроса по получению вещи");
        return item;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(Constant.HEADER_USER_ID) Long userId, @RequestBody @Valid ItemDto item) {
        log.info("Начало обработки запроса на создание вещи: {}", item);
        ItemDto newItem = itemService.createItem(userId, item);
        log.info("Окончание обработки запроса на создание вещи");
        return newItem;
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader(Constant.HEADER_USER_ID) Long userId, @PathVariable Long id, @RequestBody ItemDto item) {
        log.info("Начало обработки запроса на обновление вещи: {}", id);
        ItemDto existingItem = itemService.updateItem(userId, id, item);
        log.info("Окончание обработки запроса на обновление вещи");
        return existingItem;
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@RequestHeader(Constant.HEADER_USER_ID) Long userId, @PathVariable Long id) {
        log.info("Начало обработки запроса на удаление вещи: {}", id);
        itemService.deleteItem(userId, id);
        log.info("Окончание обработки запроса на удаление вещи");
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(Constant.HEADER_USER_ID) Long userId, @RequestParam String text) {
        log.info("Начало обработки запроса на поиск вещей по названию: {} для пользователя: {}", text, userId);
        List<ItemDto> items = itemService.searchItems(userId, text);
        log.info("Окончание обработки запроса на поиск вещей");
        return items;
    }
}
