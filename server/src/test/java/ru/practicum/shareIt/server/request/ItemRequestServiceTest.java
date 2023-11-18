package ru.practicum.shareIt.server.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareIt.server.PageRequestByElement;
import ru.practicum.shareIt.server.item.ItemRepository;
import ru.practicum.shareIt.server.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareIt.server.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareIt.server.request.dto.ItemRequestMapper;
import ru.practicum.shareIt.server.request.model.ItemRequest;
import ru.practicum.shareIt.server.user.UserRepository;
import ru.practicum.shareIt.server.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemRequestService должен")
public class ItemRequestServiceTest {
    @InjectMocks
    ItemRequestService itemRequestService;
    @Mock
    ItemRequestRepository requestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemRequestMapper requestMapper;

    private static final Long userId = 1L;
    private static final Sort sort = Sort.by(Sort.Direction.DESC, "created");
    private static final Integer from = 0;
    private static final Integer size = 1;
    private static final PageRequest pageRequest = PageRequestByElement.of(from, size, sort);

    @DisplayName("создавать запрос")
    @Test
    void createRequest_whenRequestValid_thenCreateRequest() {
        ItemRequest request = new ItemRequest();
        ItemRequestDtoRequest itemRequestDtoRequest = ItemRequestDtoRequest.builder().build();
        ItemRequestDtoResponse itemRequestDtoResponse = ItemRequestDtoResponse.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(requestMapper.toItemRequest(itemRequestDtoRequest, userId)).thenReturn(request);
        when(requestRepository.save(request)).thenReturn(request);
        when(requestMapper.toItemRequestDto(request)).thenReturn(itemRequestDtoResponse);


        ItemRequestDtoResponse result = itemRequestService.createRequest(userId, itemRequestDtoRequest);

        assertEquals(result, itemRequestDtoResponse);
        verify(requestRepository, times(1)).save(request);
    }

    @DisplayName("возвращать запросы пользователя")
    @Test
    void getItemRequests_whenItemRequestsExist_thenReturnItemRequests() {
        ItemRequest request = new ItemRequest();
        List<ItemRequest> requests = List.of(request);
        ItemRequestDtoResponse itemRequestDtoResponse = ItemRequestDtoResponse.builder().build();
        List<ItemRequestDtoResponse> itemRequestDtoResponses = List.of(itemRequestDtoResponse);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(requestRepository.findAllByUserId(userId, sort)).thenReturn(requests);
        when(requestMapper.toItemRequestDto(requests, Map.of())).thenReturn(itemRequestDtoResponses);

        List<ItemRequestDtoResponse> result = itemRequestService.getItemRequests(userId);

        assertEquals(result, itemRequestDtoResponses);
        verify(requestRepository, times(1)).findAllByUserId(userId, sort);
    }

    @DisplayName("возвращать запросы других пользователей")
    @Test
    void getOtherUsersItemRequests_whenItemRequestsExist_thenReturnItemRequests() {
        ItemRequest request = new ItemRequest();
        List<ItemRequest> requests = List.of(request);
        ItemRequestDtoResponse itemRequestDtoResponse = ItemRequestDtoResponse.builder().build();
        List<ItemRequestDtoResponse> itemRequestDtoResponses = List.of(itemRequestDtoResponse);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(requestRepository.findAllByUserIdNot(userId, pageRequest)).thenReturn(requests);
        when(requestMapper.toItemRequestDto(requests, Map.of())).thenReturn(itemRequestDtoResponses);

        List<ItemRequestDtoResponse> result = itemRequestService.getOtherUsersItemRequests(userId, from, size);

        assertEquals(result, itemRequestDtoResponses);
        verify(requestRepository, times(1)).findAllByUserIdNot(userId, pageRequest);
    }

    @DisplayName("возвращать запрос по id")
    @Test
    void getItemRequestById_whenItemExist_thenReturnItem() {
        Long requestId = 1L;
        ItemRequest request = new ItemRequest();
        ItemRequestDtoResponse itemRequestDtoResponse = ItemRequestDtoResponse.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(requestMapper.toItemRequestDto(request, List.of())).thenReturn(itemRequestDtoResponse);

        ItemRequestDtoResponse result = itemRequestService.getItemRequestById(userId, requestId);

        assertEquals(result, itemRequestDtoResponse);
        verify(requestRepository, times(1)).findById(requestId);
        verify(itemRepository, times(1)).findAllByRequestId(requestId);
    }
}
