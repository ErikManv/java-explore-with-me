package ru.practicum.exceptions;

public class DeleteObjectInUseException extends RuntimeException {

    public DeleteObjectInUseException (String className, String email) {
        super(String.format("%s - %s in use", className, email));
    }
}
