package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({ValidationException.class,
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final Exception e) {
        log.error("Ошибка при валидации {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка при валидации", e.getMessage());
    }

    @ExceptionHandler({ObjectNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final RuntimeException e) {
        log.error("Объект не найден {}", e.getMessage(), e);
        return new ErrorResponse("Объект не найден", e.getMessage());
    }

    @ExceptionHandler({ObjectExistException.class,
            DuplicateKeyException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleExistException(final RuntimeException e) {
        log.error("Объект существует {}", e.getMessage(), e);
        return new ErrorResponse("Объект существует", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Throwable e) {
        log.error("Ошибка", e);
        return new ErrorResponse("Ошибка", e.getMessage());
    }
}