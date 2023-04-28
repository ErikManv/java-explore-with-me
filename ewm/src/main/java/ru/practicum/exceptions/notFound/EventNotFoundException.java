package ru.practicum.exceptions.notFound;

public class EventNotFoundException extends NotFoundException {

        public EventNotFoundException(Long e) {
            super(String.format("Event %s not found", e));
        }
}

