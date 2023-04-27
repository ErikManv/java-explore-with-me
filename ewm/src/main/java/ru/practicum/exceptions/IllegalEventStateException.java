package ru.practicum.exceptions;

public class IllegalEventStateException extends RuntimeException {

    public IllegalEventStateException (String e) {
        super(e);
    }
}