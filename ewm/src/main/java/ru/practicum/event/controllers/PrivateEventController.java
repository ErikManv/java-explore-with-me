package ru.practicum.event.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventDto;
import ru.practicum.event.dto.EventDtoIn;
import ru.practicum.event.dto.EventDtoUpdate;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService eventServiceImpl;

    @PostMapping("/{userId}/events")
    public ResponseEntity<EventDto> addEvent(@Valid @RequestBody EventDtoIn eventDtoIn,
                                             @PathVariable Long userId) {
        return new ResponseEntity<>(eventServiceImpl.addEvent(eventDtoIn, userId), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/events")
    public ResponseEntity<List<EventDto>> getEvents(@PathVariable Long userId,
                                                    @RequestParam(value = "from", defaultValue = "0", required = false)
                                                    Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10", required = false)
                                                    Integer size) {
        return new ResponseEntity<>(eventServiceImpl.getEvents(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventDto> getEvent(@PathVariable Long userId,
                                             @PathVariable Long eventId) {
        return new ResponseEntity<>(eventServiceImpl.getEvent(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventDto> updateEvent(@RequestBody EventDtoUpdate eventDtoUpdatePrivate,
                                                @PathVariable Long userId,
                                                @PathVariable Long eventId) {
        return new ResponseEntity<>(eventServiceImpl.updateEvent(eventDtoUpdatePrivate, userId,eventId), HttpStatus.OK);
    }
}
