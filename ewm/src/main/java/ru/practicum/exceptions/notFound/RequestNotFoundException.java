package ru.practicum.exceptions.notFound;

public class RequestNotFoundException extends NotFoundException {

    public RequestNotFoundException(Long e) {
        super(String.format("Request %s not found", e));
    }
}
