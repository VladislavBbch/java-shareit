package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.Constant;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoResponse> getItems(@RequestHeader(Constant.HEADER_USER_ID) Long userId,
                                          @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                          @RequestParam(defaultValue = "10") @Min(1) @Max(30) Integer size) {
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemDtoResponse getItemById(@RequestHeader(Constant.HEADER_USER_ID) Long userId,
                                       @PathVariable Long id) {
        log.info("Начало обработки запроса по получению вещи: {}", id);
        ItemDtoResponse item = itemService.getItemById(userId, id);
        log.info("Окончание обработки запроса по получению вещи");
        return item;
    }

    @PostMapping
    public ItemDtoResponse createItem(@RequestHeader(Constant.HEADER_USER_ID) Long userId,
                                      @RequestBody @Validated({Create.class}) ItemDtoRequest item) {
        log.info("Начало обработки запроса на создание вещи: {}", item);
        ItemDtoResponse newItem = itemService.createItem(userId, item);
        log.info("Окончание обработки запроса на создание вещи");
        return newItem;
    }

    @PatchMapping("/{id}")
    public ItemDtoResponse updateItem(@RequestHeader(Constant.HEADER_USER_ID) Long userId,
                                      @PathVariable Long id,
                                      @RequestBody @Validated({Update.class}) ItemDtoRequest item) {
        log.info("Начало обработки запроса на обновление вещи: {}", id);
        ItemDtoResponse existingItem = itemService.updateItem(userId, id, item);
        log.info("Окончание обработки запроса на обновление вещи");
        return existingItem;
    }

    @Transactional
    @DeleteMapping("/{id}")
    public void deleteItem(@RequestHeader(Constant.HEADER_USER_ID) Long userId,
                           @PathVariable Long id) {
        log.info("Начало обработки запроса на удаление вещи: {}", id);
        itemService.deleteItem(userId, id);
        log.info("Окончание обработки запроса на удаление вещи");
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> searchItems(@RequestHeader(Constant.HEADER_USER_ID) Long userId,
                                             @RequestParam String text,
                                             @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                             @RequestParam(defaultValue = "10") @Min(1) @Max(30) Integer size) {
        log.info("Начало обработки запроса на поиск вещей по названию: {} для пользователя: {}", text, userId);
        List<ItemDtoResponse> items = itemService.searchItems(userId, text, from, size);
        log.info("Окончание обработки запроса на поиск вещей");
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse createComment(@RequestHeader(Constant.HEADER_USER_ID) Long userId,
                                            @PathVariable Long itemId,
                                            @RequestBody @Valid CommentDtoRequest comment) {
        log.info("Начало обработки запроса на создание комментария: {}", comment.getText());
        CommentDtoResponse newComment = itemService.createComment(userId, itemId, comment);
        log.info("Окончание обработки запроса на создание комментария");
        return newComment;
    }
}
