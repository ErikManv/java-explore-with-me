package ru.practicum.event.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.enums.SortValueEvents;
import ru.practicum.event.dto.EventDto;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class PublicEventController {

    private final EventService eventServiceImpl;


    @GetMapping
    public ResponseEntity<List<EventDto>> getEventsParamPublic(@RequestParam(required = false, name = "text") String text,
                                                               @RequestParam(required = false, name = "categories") List<Long> categories,
                                                               @RequestParam(required = false, name = "paid") Boolean paid,
                                                               @RequestParam(required = false, name = "onlyAvailable") Boolean onlyAvailable,
                                                               @RequestParam(required = false, name = "sort") SortValueEvents sort,
                                                               @RequestParam(required = false, name = "rangeStart") String rangeStart,
                                                               @RequestParam(required = false, name = "rangeEnd") String rangeEnd,
                                                               @RequestParam(value = "from", defaultValue = "0", required = false)
                                                                   Integer from,
                                                               @RequestParam(value = "size", defaultValue = "10", required = false)
                                                                   Integer size,
                                                               HttpServletRequest request) {
        return new ResponseEntity<>(eventServiceImpl.getEventsParamPublic(text, categories, paid, rangeStart,
            rangeEnd, onlyAvailable, sort, from, size, request), HttpStatus.OK);
    }

    @GetMapping("/{eventId}")
    public  ResponseEntity<EventDto> getEventById(@PathVariable(name = "eventId") Long eventId, HttpServletRequest request) {
        return new ResponseEntity<>(eventServiceImpl.getEventById(eventId, request), HttpStatus.OK);
    }
}
