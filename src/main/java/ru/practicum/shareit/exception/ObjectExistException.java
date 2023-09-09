package ru.practicum.shareit.exception;

public class ObjectExistException extends RuntimeException {
    public ObjectExistException(final String message) {
        super(message);
    }
}
