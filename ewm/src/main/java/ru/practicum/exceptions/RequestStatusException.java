package ru.practicum.exceptions;

public class RequestStatusException extends RuntimeException {

    public RequestStatusException(String e) {
        super(e);
    }
}