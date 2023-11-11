package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constant;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDtoResponse createRequest(@RequestHeader(Constant.HEADER_USER_ID) Long userId,
                                                @RequestBody @Valid ItemRequestDtoRequest request) {
        log.info("Начало обработки запроса на создание запроса на вещь: {}", request.getDescription());
        ItemRequestDtoResponse newRequest = requestService.createRequest(userId, request);
        log.info("Окончание обработки запроса на создание запроса на вещь: {}", request.getDescription());
        return newRequest;
    }

    @GetMapping
    public List<ItemRequestDtoResponse> getItemRequests(@RequestHeader(Constant.HEADER_USER_ID) Long userId) {
        log.info("Начало обработки запроса по получению всех запросов пользователя: {}", userId);
        List<ItemRequestDtoResponse> requests = requestService.getItemRequests(userId);
        log.info("Окончание обработки запроса по получению всех запросов пользователя: {}", userId);
        return requests;
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> getOtherUsersItemRequests(
            @RequestHeader(Constant.HEADER_USER_ID) Long userId,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) @Max(30) Integer size) {
        log.info("Начало обработки запроса по получению всех запросов других пользователей кроме: {}", userId);
        List<ItemRequestDtoResponse> requests = requestService.getOtherUsersItemRequests(userId, from, size);
        log.info("Окончание обработки запроса по получению всех запросов других пользователей кроме: {}", userId);
        return requests;
    }

    @GetMapping("/{id}")
    public ItemRequestDtoResponse getItemRequestById(@RequestHeader(Constant.HEADER_USER_ID) Long userId,
                                                     @PathVariable Long id) {
        log.info("Начало обработки запроса по получению запроса: {}", id);
        ItemRequestDtoResponse request = requestService.getItemRequestById(userId, id);
        log.info("Окончание обработки запроса по получению запроса: {}", id);
        return request;
    }

}
