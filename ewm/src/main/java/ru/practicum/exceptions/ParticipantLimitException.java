package ru.practicum.exceptions;

public class ParticipantLimitException extends RuntimeException {

    public ParticipantLimitException() {
        super("Participants limit reached");
    }
}