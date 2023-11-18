package ru.practicum.shareit.gateway.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.Constant;
import ru.practicum.shareit.gateway.Create;
import ru.practicum.shareit.gateway.Update;
import ru.practicum.shareit.gateway.item.dto.CommentDtoRequest;
import ru.practicum.shareit.gateway.item.dto.ItemDtoRequest;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(Constant.HEADER_USER_ID) Long userId,
                                           @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(defaultValue = "10") @Min(1) @Max(30) Integer size) {
        log.info("Начало обработки запроса на получение всех вещей");
        ResponseEntity<Object> items = itemClient.getItems(userId, from, size);
        log.info("Окончание обработки запроса на получение всех вещей");
        return items;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@RequestHeader(Constant.HEADER_USER_ID) Long userId,
                                       @PathVariable Long id) {
        log.info("Начало обработки запроса по получению вещи: {}", id);
        ResponseEntity<Object> item = itemClient.getItemById(userId, id);
        log.info("Окончание обработки запроса по получению вещи");
        return item;
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(Constant.HEADER_USER_ID) Long userId,
                                      @RequestBody @Validated({Create.class}) ItemDtoRequest item) {
        log.info("Начало обработки запроса на создание вещи: {}", item);
        ResponseEntity<Object> newItem = itemClient.createItem(userId, item);
        log.info("Окончание обработки запроса на создание вещи");
        return newItem;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader(Constant.HEADER_USER_ID) Long userId,
                                      @PathVariable Long id,
                                      @RequestBody @Validated({Update.class}) ItemDtoRequest item) {
        log.info("Начало обработки запроса на обновление вещи: {}", id);
        ResponseEntity<Object> existingItem = itemClient.updateItem(userId, id, item);
        log.info("Окончание обработки запроса на обновление вещи");
        return existingItem;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@RequestHeader(Constant.HEADER_USER_ID) Long userId,
                           @PathVariable Long id) {
        log.info("Начало обработки запроса на удаление вещи: {}", id);
        ResponseEntity<Object> result = itemClient.deleteItem(userId, id);
        log.info("Окончание обработки запроса на удаление вещи");
        return result;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(Constant.HEADER_USER_ID) Long userId,
                                             @RequestParam String text,
                                             @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                             @RequestParam(defaultValue = "10") @Min(1) @Max(30) Integer size) {
        log.info("Начало обработки запроса на поиск вещей по названию: {} для пользователя: {}", text, userId);
        if (text.isBlank()) {
            return new ResponseEntity<>(List.of(), HttpStatus.OK);
        }
        ResponseEntity<Object> items = itemClient.searchItems(userId, text, from, size);
        log.info("Окончание обработки запроса на поиск вещей");
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(Constant.HEADER_USER_ID) Long userId,
                                            @PathVariable Long itemId,
                                            @RequestBody @Valid CommentDtoRequest comment) {
        log.info("Начало обработки запроса на создание комментария: {}", comment.getText());
        ResponseEntity<Object> newComment = itemClient.createComment(userId, itemId, comment);
        log.info("Окончание обработки запроса на создание комментария");
        return newComment;
    }
}
