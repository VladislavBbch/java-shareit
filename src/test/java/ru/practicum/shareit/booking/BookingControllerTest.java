package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Constant;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@DisplayName("BookingController должен")
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    private static final Long bookerId = 1L;

    @DisplayName("создавать бронирование")
    @SneakyThrows
    @Test
    void shouldCreateBooking() {
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusHours(1))
                .build();
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder().build();

        when(bookingService.createBooking(bookerId, bookingDtoRequest))
                .thenReturn(bookingDtoResponse);

        mockMvc.perform(post("/bookings")
                        .header(Constant.HEADER_USER_ID, bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoResponse)));

        verify(bookingService, times(1)).createBooking(bookerId, bookingDtoRequest);
    }

    @DisplayName("валидировать id вещи при создании бронирования")
    @SneakyThrows
    @Test
    void shouldValidateItemId_CreateBooking() {
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusHours(1))
                .build();

        mockMvc.perform(post("/bookings")
                        .header(Constant.HEADER_USER_ID, bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).createBooking(bookerId, bookingDtoRequest);
    }

    @DisplayName("валидировать время начала при создании бронирования")
    @SneakyThrows
    @Test
    void shouldValidateStart_CreateBooking() {
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusMinutes(1))
                .end(LocalDateTime.now().plusHours(1))
                .build();

        mockMvc.perform(post("/bookings")
                        .header(Constant.HEADER_USER_ID, bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).createBooking(bookerId, bookingDtoRequest);
    }

    @DisplayName("валидировать, что время начала до времени окончания при создании бронирования")
    @SneakyThrows
    @Test
    void shouldValidateStartBeforeEnd_CreateBooking() {
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().minusHours(1))
                .build();

        mockMvc.perform(post("/bookings")
                        .header(Constant.HEADER_USER_ID, bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).createBooking(bookerId, bookingDtoRequest);
    }

    @DisplayName("подтверждать бронирование")
    @SneakyThrows
    @Test
    void shouldApproveBooking() {
        Long bookingId = 1L;
        Boolean isApproved = true;
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder().build();

        when(bookingService.approveBooking(bookerId, bookingId, isApproved))
                .thenReturn(bookingDtoResponse);

        mockMvc.perform(patch("/bookings/{id}", bookingId)
                        .header(Constant.HEADER_USER_ID, bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(isApproved)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoResponse)));

        verify(bookingService, times(1)).approveBooking(bookerId, bookingId, isApproved);
    }

    @DisplayName("возвращать бронирования")
    @SneakyThrows
    @Test
    void shouldGetBookings() {
        BookingState state = BookingState.CURRENT;
        Integer from = 1;
        Integer size = 2;
        List<BookingDtoResponse> bookings = List.of();

        when(bookingService.getBookings(bookerId, state, from, size))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header(Constant.HEADER_USER_ID, bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", String.valueOf(state))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));

        verify(bookingService, times(1)).getBookings(bookerId, state, from, size);
    }

    @DisplayName("валидировать значение from при возвращении бронирований")
    @SneakyThrows
    @Test
    void shouldValidateFrom_GetBookings() {
        BookingState state = BookingState.CURRENT;
        Integer from = -1;
        Integer size = 2;

        mockMvc.perform(get("/bookings")
                        .header(Constant.HEADER_USER_ID, bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", String.valueOf(state))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBookings(bookerId, state, from, size);
    }

    @DisplayName("валидировать значение size (min) при возвращении бронирований")
    @SneakyThrows
    @Test
    void shouldValidateSizeMin_GetBookings() {
        BookingState state = BookingState.CURRENT;
        Integer from = 1;
        Integer size = 0;

        mockMvc.perform(get("/bookings")
                        .header(Constant.HEADER_USER_ID, bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", String.valueOf(state))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBookings(bookerId, state, from, size);
    }

    @DisplayName("валидировать значение size (max) при возвращении бронирований")
    @SneakyThrows
    @Test
    void shouldValidateSizeMax_GetBookings() {
        BookingState state = BookingState.CURRENT;
        Integer from = 1;
        Integer size = 31;

        mockMvc.perform(get("/bookings")
                        .header(Constant.HEADER_USER_ID, bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", String.valueOf(state))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBookings(bookerId, state, from, size);
    }

    @DisplayName("возвращать бронирования владельца")
    @SneakyThrows
    @Test
    void shouldGetOwnerBookings() {
        BookingState state = BookingState.CURRENT;
        Integer from = 1;
        Integer size = 2;
        List<BookingDtoResponse> bookings = List.of();

        when(bookingService.getOwnerBookings(bookerId, state, from, size))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(Constant.HEADER_USER_ID, bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", String.valueOf(state))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));

        verify(bookingService, times(1)).getOwnerBookings(bookerId, state, from, size);
    }

    @DisplayName("валидировать значение from при возвращении бронирований владельца")
    @SneakyThrows
    @Test
    void shouldValidateFrom_GetOwnerBookings() {
        BookingState state = BookingState.CURRENT;
        Integer from = -1;
        Integer size = 2;

        mockMvc.perform(get("/bookings/owner")
                        .header(Constant.HEADER_USER_ID, bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", String.valueOf(state))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getOwnerBookings(bookerId, state, from, size);
    }

    @DisplayName("валидировать значение size (min) при возвращении бронирований владельца")
    @SneakyThrows
    @Test
    void shouldValidateSizeMin_GetOwnerBookings() {
        BookingState state = BookingState.CURRENT;
        Integer from = 1;
        Integer size = 0;

        mockMvc.perform(get("/bookings/owner")
                        .header(Constant.HEADER_USER_ID, bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", String.valueOf(state))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getOwnerBookings(bookerId, state, from, size);
    }

    @DisplayName("валидировать значение size (max) при возвращении бронирований владельца")
    @SneakyThrows
    @Test
    void shouldValidateSizeMax_GetOwnerBookings() {
        BookingState state = BookingState.CURRENT;
        Integer from = 1;
        Integer size = 31;

        mockMvc.perform(get("/bookings/owner")
                        .header(Constant.HEADER_USER_ID, bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", String.valueOf(state))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getOwnerBookings(bookerId, state, from, size);
    }

    @DisplayName("возвращать бронирование по id")
    @SneakyThrows
    @Test
    void shouldGetBookingById() {
        Long bookingId = 1L;
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder().build();

        when(bookingService.getBookingById(bookerId, bookingId))
                .thenReturn(bookingDtoResponse);

        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header(Constant.HEADER_USER_ID, bookerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoResponse)));

        verify(bookingService, times(1)).getBookingById(bookerId, bookingId);
    }
}
