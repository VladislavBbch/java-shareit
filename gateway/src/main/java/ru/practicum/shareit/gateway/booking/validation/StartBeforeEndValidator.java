package ru.practicum.shareit.gateway.booking.validation;

import ru.practicum.shareit.gateway.booking.dto.BookingDtoRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookingDtoRequest> {
    @Override
    public boolean isValid(BookingDtoRequest bookingDto, ConstraintValidatorContext context) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}
