package ru.practicum.shareIt.server.exception;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(final String message) {
        super(message);
    }
}