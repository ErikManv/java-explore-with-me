package ru.practicum.exceptions.notFound;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long e) {
        super(String.format("User %s not found", e));
    }
}
