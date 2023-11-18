package ru.practicum.shareit.gateway.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.Constant;
import ru.practicum.shareit.gateway.request.dto.ItemRequestDtoRequest;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(Constant.HEADER_USER_ID) Long userId,
                                                @RequestBody @Valid ItemRequestDtoRequest request) {
        log.info("Начало обработки запроса на создание запроса на вещь: {}", request.getDescription());
        ResponseEntity<Object> newRequest = requestClient.createRequest(userId, request);
        log.info("Окончание обработки запроса на создание запроса на вещь: {}", request.getDescription());
        return newRequest;
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader(Constant.HEADER_USER_ID) Long userId) {
        log.info("Начало обработки запроса по получению всех запросов пользователя: {}", userId);
        ResponseEntity<Object> requests = requestClient.getItemRequests(userId);
        log.info("Окончание обработки запроса по получению всех запросов пользователя: {}", userId);
        return requests;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersItemRequests(
            @RequestHeader(Constant.HEADER_USER_ID) Long userId,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) @Max(30) Integer size) {
        log.info("Начало обработки запроса по получению всех запросов других пользователей кроме: {}", userId);
        ResponseEntity<Object> requests = requestClient.getOtherUsersItemRequests(userId, from, size);
        log.info("Окончание обработки запроса по получению всех запросов других пользователей кроме: {}", userId);
        return requests;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(Constant.HEADER_USER_ID) Long userId,
                                                     @PathVariable Long id) {
        log.info("Начало обработки запроса по получению запроса: {}", id);
        ResponseEntity<Object> request = requestClient.getItemRequestById(userId, id);
        log.info("Окончание обработки запроса по получению запроса: {}", id);
        return request;
    }

}
