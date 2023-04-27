package ru.practicum.event.service;

import org.springframework.stereotype.Service;
import ru.practicum.enums.SortValueEvents;
import ru.practicum.event.dto.EventDto;
import ru.practicum.event.dto.EventDtoIn;
import ru.practicum.event.dto.EventDtoUpdate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public interface EventService {

    EventDto addEvent(EventDtoIn eventDtoIn, Long userId);
    List<EventDto> getEventsParamAdmin(List<Long> ids, List<String> states, List<Long> categories, String start, String end,
                              Integer from ,Integer size);

    EventDto updateEventAdmin(EventDtoUpdate eventDtoUpdateAdmin, Long eventId);

    List<EventDto> getEvents(Long userId, Integer from, Integer size);

    EventDto getEvent(Long userId, Long eventId);

    EventDto updateEvent(EventDtoUpdate eventDtoUpdatePrivate, Long userId, Long eventId);


    List<EventDto> getEventsParamPublic(String text, List<Long> categoriesId, Boolean paid, String rangeStart,
                                        String rangeEnd, Boolean onlyAvailable, SortValueEvents sort, Integer from, Integer size,
                                        HttpServletRequest request);

    EventDto getEventById(Long eventId, HttpServletRequest request);
}
