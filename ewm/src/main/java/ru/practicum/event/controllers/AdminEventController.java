package ru.practicum.event.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventDto;
import ru.practicum.event.dto.EventDtoUpdate;
import ru.practicum.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final EventService eventServiceImpl;

    @GetMapping
    public ResponseEntity<List<EventDto>> getEventsParamAdmin(@RequestParam(required = false, name = "users") List<Long> users,
                                                                  @RequestParam(required = false, name = "states") List<String> states,
                                                                  @RequestParam(required = false, name = "categories") List<Long> categories,
                                                                  @RequestParam(required = false, name = "rangeStart") String rangeStart,
                                                                  @RequestParam(required = false, name = "rangeEnd") String rangeEnd,
                                                                  @RequestParam(value = "from", defaultValue = "0", required = false)
                                                                  Integer from,
                                                                  @RequestParam(value = "size", defaultValue = "10", required = false)
                                                                      Integer size) {
        return new ResponseEntity<>(eventServiceImpl.getEventsParamAdmin(users, states, categories, rangeStart, rangeEnd, from, size), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventDto> updateEvent(@RequestBody EventDtoUpdate eventDtoUpdateAdmin,
                                                @PathVariable(name = "eventId") Long eventId) {
        return new ResponseEntity<>(eventServiceImpl.updateEventAdmin(eventDtoUpdateAdmin, eventId), HttpStatus.OK);
    }
}
