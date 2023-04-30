package ru.practicum.exceptions;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String className, String info) {
        super(String.format("%s - %s already exists", className, info));
    }
}