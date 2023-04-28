package ru.practicum.exceptions.notFound;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(Long e) {
        super(String.format("User %s not found", e));
    }
}
