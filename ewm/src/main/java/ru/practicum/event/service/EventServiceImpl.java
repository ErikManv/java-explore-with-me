package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.Pattern;
import ru.practicum.category.model.Category;
import ru.practicum.category.dao.CategoryRepository;
import ru.practicum.event.controllers.AdminEventController;
import ru.practicum.enums.EventState;
import ru.practicum.enums.SortValueEvents;
import ru.practicum.enums.StateActionForAdmin;
import ru.practicum.enums.StateActionForUser;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.dto.EventDto;
import ru.practicum.event.dto.EventDtoIn;
import ru.practicum.event.dto.EventDtoUpdate;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.exceptions.*;
import ru.practicum.exceptions.StateException;
import ru.practicum.exceptions.notFound.CategoryNotFoundException;
import ru.practicum.exceptions.notFound.EventNotFoundException;
import ru.practicum.exceptions.notFound.UserNotFoundException;
import ru.practicum.statsdto.HitDtoInput;
import ru.practicum.user.model.User;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.statsclient.StatsClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Pattern.DATE);
    private final EventMapper eventMapper;
    private final StatsClient statsClient;
    private static final Logger log = LoggerFactory.getLogger(AdminEventController.class);

    //////////////////////////////////PRIVATE
    @Override
    public  List<EventDto> getEvents(Long userId, Integer from, Integer size) {
        getUser(userId);
        return eventMapper.toEventDtoList(eventRepository.findEventsByInitiator(userId, from, size));
    }

    @Override
    public EventDto addEvent(EventDtoIn eventDtoIn, Long userId) {
        if (eventDtoIn.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new DateException("Event date can't be later than 2 hours before the start");
        }
        Event event = eventMapper.toEventModel(eventDtoIn);
        if (eventDtoIn.getRequestModeration() != null) {
            event.setRequestModeration(eventDtoIn.getRequestModeration());
        }
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(getUser(userId));
        event.setState(EventState.PENDING);
        event.setViews(0L);
        log.info("event {} created", event.getAnnotation());
        return eventMapper.toEventDto(eventRepository.save(event));
    }

    @Override
    public EventDto getEvent(Long userId, Long eventId) {
        getEvent(eventId);
        getUser(userId);
        return eventMapper.toEventDto(eventRepository.findEventByInitiatorIdAndId(userId, eventId));
    }

    @Override
    public EventDto updateEvent(EventDtoUpdate eventDtoUpdateAdmin, Long userId, Long eventId) {
        getUser(userId);
        Event event = getEvent(eventId);
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new DuplicateException("Event", eventId.toString());
        }
        event = eventCompose(eventDtoUpdateAdmin, eventId);

        if (eventDtoUpdateAdmin.getStateAction() != null) {
            if (eventDtoUpdateAdmin.getStateAction().equals(StateActionForUser.SEND_TO_REVIEW.toString())) {
                event.setState(EventState.PENDING);
            } else {
                event.setState(EventState.CANCELED);
            }
        }
        return eventMapper.toEventDto(eventRepository.save(event));

    }

/////////////////////////////////////////ADMIN
    @Override
    public EventDto updateEventAdmin(EventDtoUpdate eventDtoUpdateAdmin, Long eventId) {
        Event event = eventCompose(eventDtoUpdateAdmin, eventId);
        if (eventDtoUpdateAdmin.getStateAction().equals(StateActionForAdmin.PUBLISH_EVENT.toString())) {
            if (event.getState().equals(EventState.PUBLISHED)) {
                throw new StateException("already PUBLISHED");
            } else if (event.getState().equals(EventState.CANCELED)) {
                throw new StateException("already CANCELED");
            } else {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
        }

        if (eventDtoUpdateAdmin.getStateAction().equals(StateActionForAdmin.REJECT_EVENT.toString())) {
            if (event.getState().equals(EventState.PUBLISHED)) {
                throw new StateException("already PUBLISHED");
            } else if (event.getState().equals(EventState.CANCELED)) {
                throw new StateException("already CANCELED");
            } else {
                event.setState(EventState.CANCELED);
            }
        }
        return eventMapper.toEventDto(eventRepository.save(event));
    }

    @Override
    public List<EventDto> getEventsParamAdmin(List<Long> users, List<String> states, List<Long> categoriesId, String rangeStart,
                                     String rangeEnd, Integer from, Integer size) {
        if (states != null) {
            return eventMapper.toEventDtoList(eventRepository.searchByAdmin(users, states, categoriesId,
                parseStringToDate(rangeStart), parseStringToDate(rangeEnd), from, size));
        } else {
            return eventMapper.toEventDtoList(eventRepository.searchWithoutStateByAdmin(users,categoriesId,
                parseStringToDate(rangeStart), parseStringToDate(rangeEnd), from, size));

        }
    }
    //////////////////////////////////////PUBLIC

    @Override
    public List<EventDto> getEventsParamPublic(String text, List<Long> categoriesId, Boolean paid, String rangeStart,
                                               String rangeEnd, Boolean onlyAvailable, SortValueEvents sort,
                                               Integer from, Integer size, HttpServletRequest request) {

        statsClient.hit(HitDtoInput.builder()
            .app("ewm")
            .ip(request.getRemoteAddr())
            .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Pattern.DATE)))
            .uri(request.getRequestURI())
            .build());

        return eventRepository.searchPublic(text, categoriesId, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size).stream()
            .map(eventMapper::toEventDto)
            .collect(Collectors.toList());
    }

    @Override
    public EventDto getEventById(Long eventId, HttpServletRequest request) {

        Event event = getEvent(eventId);
        statsClient.hit(HitDtoInput.builder()
            .app("ewm")
            .ip(request.getRemoteAddr())
            .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Pattern.DATE)))
            .uri(request.getRequestURI())
            .build());

        event.setViews(event.getViews() + 1);
        return eventMapper.toEventDto(eventRepository.save(event));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private LocalDateTime parseStringToDate(String stringTime) {
        if (stringTime != null) {
            return LocalDateTime.parse(stringTime, dateFormatter);
        } else return null;
    }

    private Event eventCompose(EventDtoUpdate eventDtoUpdateAdmin, Long eventId) {
        Event event = getEvent(eventId);
        if (eventDtoUpdateAdmin.getAnnotation() != null) event.setAnnotation(eventDtoUpdateAdmin.getAnnotation());
        if (eventDtoUpdateAdmin.getCategory() != null) event.setCategory(getCategory(eventDtoUpdateAdmin.getCategory()));
        if (eventDtoUpdateAdmin.getDescription() != null) event.setDescription(eventDtoUpdateAdmin.getDescription());
        if (eventDtoUpdateAdmin.getEventDate() != null) {
            if (eventDtoUpdateAdmin.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new DateException("Event date can't be later than 2 hours before the start");
            }
            event.setEventDate(eventDtoUpdateAdmin.getEventDate());
        }
        if (eventDtoUpdateAdmin.getLocation() != null) event.setLocation(eventDtoUpdateAdmin.getLocation());
        if (eventDtoUpdateAdmin.getPaid() != null) event.setPaid(eventDtoUpdateAdmin.getPaid());
        if (eventDtoUpdateAdmin.getParticipantLimit() != null) event.setParticipantLimit(eventDtoUpdateAdmin.getParticipantLimit());
        if (eventDtoUpdateAdmin.getRequestModeration() != null) event.setRequestModeration(eventDtoUpdateAdmin.getRequestModeration());
        if (eventDtoUpdateAdmin.getTitle() != null) event.setTitle(eventDtoUpdateAdmin.getTitle());
        return event;
    }
}
