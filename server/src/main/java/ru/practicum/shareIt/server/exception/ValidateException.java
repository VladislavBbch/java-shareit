package ru.practicum.shareIt.server.exception;

public class ValidateException extends RuntimeException {
    public ValidateException(final String message) {
        super(message);
    }
}
