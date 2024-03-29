package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.Pattern;
import ru.practicum.category.model.Category;
import ru.practicum.category.dao.CategoryRepository;
import ru.practicum.event.controllers.AdminEventController;
import ru.practicum.enums.EventState;
import ru.practicum.enums.SortValueEvents;
import ru.practicum.enums.StateActionForAdmin;
import ru.practicum.enums.StateActionForUser;
import ru.practicum.event.controllers.PrivateEventController;
import ru.practicum.event.controllers.PublicEventController;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.dto.EventDto;
import ru.practicum.event.dto.EventDtoIn;
import ru.practicum.event.dto.EventDtoUpdate;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.exceptions.*;
import ru.practicum.exceptions.EventStateException;
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

    private static final Logger logAdmin = LoggerFactory.getLogger(AdminEventController.class);
    private static final Logger logPrivate = LoggerFactory.getLogger(PrivateEventController.class);
    private static final Logger logPublic = LoggerFactory.getLogger(PublicEventController.class);

    @Override
    public  List<EventDto> getEvents(Long userId, Integer from, Integer size) {
        User user =  getUser(userId);
        logPrivate.info("events list was returned");
        return eventMapper.toEventDtoList(eventRepository.findEventsByInitiator(user, PageRequest.of(from, size)));
    }

    @Override
    public EventDto addEvent(EventDtoIn eventDtoIn, Long userId) {
        if (eventDtoIn.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new DateException("event date can't be later than 2 hours before the start");
        }
        Event event = eventMapper.toEventModel(eventDtoIn);
        if (eventDtoIn.getRequestModeration() != null) {
            event.setRequestModeration(eventDtoIn.getRequestModeration());
        }
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(getUser(userId));
        event.setState(EventState.PENDING);
        event.setViews(0L);
        logPrivate.info("event {} created", event.getAnnotation());
        return eventMapper.toEventDto(eventRepository.save(event));
    }

    @Override
    public EventDto getEvent(Long userId, Long eventId) {
        getEvent(eventId);
        getUser(userId);
        logPrivate.info("event {} was returned", eventId);
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
        logPrivate.info("event {} was updated", eventId);
        return eventMapper.toEventDto(eventRepository.save(event));

    }

    @Override
    public EventDto updateEventAdmin(EventDtoUpdate eventDtoUpdateAdmin, Long eventId) {
        Event event = eventCompose(eventDtoUpdateAdmin, eventId);
        if (eventDtoUpdateAdmin.getStateAction().equals(StateActionForAdmin.PUBLISH_EVENT.toString())) {
            if (event.getState().equals(EventState.PUBLISHED)) {
                throw new EventStateException("already PUBLISHED");
            } else if (event.getState().equals(EventState.CANCELED)) {
                throw new EventStateException("already CANCELED");
            } else {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
        }

        if (eventDtoUpdateAdmin.getStateAction().equals(StateActionForAdmin.REJECT_EVENT.toString())) {
            if (event.getState().equals(EventState.PUBLISHED)) {
                throw new EventStateException("already PUBLISHED");
            } else if (event.getState().equals(EventState.CANCELED)) {
                throw new EventStateException("already CANCELED");
            } else {
                event.setState(EventState.CANCELED);
            }
        }
        logAdmin.info("event {} was updated", eventId);
        return eventMapper.toEventDto(eventRepository.save(event));
    }

    @Override
    public List<EventDto> getEventsParamAdmin(List<Long> users, List<String> states, List<Long> categoriesId, String rangeStart,
                                     String rangeEnd, Integer from, Integer size) {
        if (states != null) {
            List<EventState> statesEnum = states.stream().map(EventState::valueOf).collect(Collectors.toList());
            logPrivate.info("events list was returned with states filter");
            return eventMapper.toEventDtoList(eventRepository.searchByAdmin(userRepository.findUsersByIdIn(users),
                statesEnum, categoryRepository.findCategoriesByIdIn(categoriesId),
                parseStringToDate(rangeStart), parseStringToDate(rangeEnd), PageRequest.of(from, size)));
        } else {
            logPrivate.info("events list was returned without states filter");
            return eventMapper.toEventDtoList(eventRepository.searchWithoutStateByAdmin(userRepository.findUsersByIdIn(users),
                categoryRepository.findCategoriesByIdIn(categoriesId),
                parseStringToDate(rangeStart), parseStringToDate(rangeEnd), PageRequest.of(from, size)));

        }
    }

    @Override
    public List<EventDto> getEventsParamPublic(String text, List<Long> categoriesId, Boolean paid, String rangeStart,
                                               String rangeEnd, Boolean onlyAvailable, SortValueEvents sort,
                                               Integer from, Integer size, HttpServletRequest request) {
        List<Event> eventList;
        statsClient.hit(HitDtoInput.builder()
            .app("ewm")
            .ip(request.getRemoteAddr())
            .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Pattern.DATE)))
            .uri(request.getRequestURI())
            .build());
        if (sort != null) {
            if (sort.equals(SortValueEvents.EVENT_DATE)) {
                logPublic.info("events list ordered by event date was returned");
                eventList = eventRepository.searchPublic(text, categoryRepository.findCategoriesByIdIn(categoriesId), paid,
                        parseStringToDate(rangeStart), parseStringToDate(rangeEnd),
                        PageRequest.of(from, size, Sort.by("eventDate").ascending()));
            } else {
                logPublic.info("events list ordered by views was returned");
                eventList =
                    eventRepository.searchPublic(text, categoryRepository.findCategoriesByIdIn(categoriesId), paid,
                        parseStringToDate(rangeStart), parseStringToDate(rangeEnd),
                        PageRequest.of(from, size, Sort.by("views").ascending()));
            }
        } else {
            eventList = eventRepository.searchPublic(text, categoryRepository.findCategoriesByIdIn(categoriesId), paid,
                    parseStringToDate(rangeStart), parseStringToDate(rangeEnd),
                    PageRequest.of(from, size));
        }

        if (onlyAvailable != null) {
            eventList.stream().filter(x -> x.getConfirmedRequests() < x.getParticipantLimit())
                .collect(Collectors.toList());
        }
        return eventList.stream()
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
        logPublic.info("event {} was returned", eventId);
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
                throw new DateException("event date can't be later than 2 hours before the start");
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
