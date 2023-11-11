package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {
    private final ItemMapper itemMapper;

    public ItemRequest toItemRequest(ItemRequestDtoRequest requestDto, Long userId) {
        return ItemRequest.builder()
                .description(requestDto.getDescription())
                .user(User.builder().id(userId).build())
                .build();
    }

    public ItemRequestDtoResponse toItemRequestDto(ItemRequest request) {
        return toItemRequestDto(request, List.of());
    }

    public ItemRequestDtoResponse toItemRequestDto(ItemRequest request, List<Item> requestsItems) {
        return ItemRequestDtoResponse.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(itemMapper.toItemDto(requestsItems))
                .build();
    }

    public List<ItemRequestDtoResponse> toItemRequestDto(List<ItemRequest> requests, Map<Long, List<Item>> requestsItems) {
        return requests.stream()
                .map(request -> toItemRequestDto(request, requestsItems.getOrDefault(request.getId(), List.of())))
                .collect(Collectors.toList());
    }
}
