package ru.practicum.exceptions.notFound;

public class EventNotFoundException extends RuntimeException {

        public EventNotFoundException(Long e) {
            super(String.format("Event %s not found", e));
        }
}

