package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.PageRequestByElement;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper requestMapper;

    public ItemRequestDtoResponse createRequest(Long userId, ItemRequestDtoRequest requestDto) {
        checkUser(userId);
        ItemRequest request = requestMapper.toItemRequest(requestDto, userId);
        return requestMapper.toItemRequestDto(requestRepository.save(request));
    }

    public List<ItemRequestDtoResponse> getItemRequests(Long userId) {
        checkUser(userId);
        List<ItemRequest> requests = requestRepository.findAllByUserId(userId, Sort.by(Sort.Direction.DESC, "created"));
        return requestMapper.toItemRequestDto(requests, getItemsForRequests(requests));
    }

    public List<ItemRequestDtoResponse> getOtherUsersItemRequests(Long userId, Integer from, Integer size) {
        checkUser(userId);
        List<ItemRequest> requests = requestRepository.findAllByUserIdNot(userId,
                PageRequestByElement.of(from, size, Sort.by(Sort.Direction.DESC, "created")));
        return requestMapper.toItemRequestDto(requests, getItemsForRequests(requests));
    }

    public ItemRequestDtoResponse getItemRequestById(Long userId, Long requestId) {
        checkUser(userId);
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id запроса: " + requestId));
        List<Item> requestItems = itemRepository.findAllByRequestId(requestId);
        return requestMapper.toItemRequestDto(request, requestItems);
    }

    private void checkUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id пользователя: " + userId));
    }

    private Map<Long, List<Item>> getItemsForRequests(List<ItemRequest> requests) {
        List<Long> ids = requests.stream().map(ItemRequest::getId).collect(toList());
        return itemRepository.findAllByRequestIdIn(ids).stream()
                .collect(groupingBy(Item::getRequestId, toList()));
    }
}
