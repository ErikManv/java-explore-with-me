package ru.practicum.exceptions;

public class PermissionException extends RuntimeException {

    public PermissionException(Long e) {
        super(String.format("user %s author owner", e));
    }

}
