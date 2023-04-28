package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.enums.EventState;
import ru.practicum.enums.RequestStatus;
import ru.practicum.enums.RequestStatusToUpdate;
import ru.practicum.event.model.Event;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.exceptions.*;
import ru.practicum.exceptions.notFound.EventNotFoundException;
import ru.practicum.exceptions.notFound.RequestNotFoundException;
import ru.practicum.exceptions.notFound.UserNotFoundException;
import ru.practicum.request.controllers.PrivateRequestController;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestStatusResultDto;
import ru.practicum.request.dto.RequestStatusUpdateDto;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.user.model.User;
import ru.practicum.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    private static final Logger log = LoggerFactory.getLogger(PrivateRequestController.class);

    @Override
    public List<RequestDto> getUserRequests(Long userId) {
        getUser(userId);
        log.info("requests from user {} returned", userId);
        return requestMapper.toRequestDtoList(requestRepository.findRequestsByRequesterId(userId));
    }

    @Override
    public RequestDto addRequest(Long eventId, Long userId) {
        Request requestCheck = requestRepository.findRequestByEventAndRequester(getEvent(eventId), getUser(userId));
        if (requestCheck != null) {
            throw new DuplicateException("request", requestCheck.getId().toString());
        } else {
            Event event = getEvent(eventId);
            if (userId.equals(event.getInitiator().getId())) {
                throw new UserRequestOwnEventException("Unable to create request for own event");
            }

            if (event.getState() != EventState.PUBLISHED) {
                throw new EventStateException("event state not PUBLISHED");
            }

            List<Request> requests = requestRepository.findAllByEventId(eventId);

            if (!event.getRequestModeration() && requests.size() >= event.getParticipantLimit()) {
                throw new ParticipantLimitException();
            }

            Request request = Request.builder()
                .event(getEvent(eventId))
                .requester(getUser(userId))
                .created(LocalDateTime.now())
                .status(RequestStatus.PENDING)
                .build();

            if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
                request.setStatus(RequestStatus.CONFIRMED);
            }

            log.info("request from user {} to event {} was  created", userId, eventId);
            return requestMapper.toDto(requestRepository.save(request));
        }
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        Request request = getRequest(requestId);
        request.setStatus(RequestStatus.CANCELED);
        log.info("request {} was canceled by user {}", requestId, userId);
        return  requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getEventParticipants(Long userId, Long eventId) {
        log.info("requests list was returned");
        return requestMapper.toRequestDtoList(requestRepository.getEventParticipants(userId, eventId));
    }

    @Override
    public RequestStatusResultDto changeRequestStatus(RequestStatusUpdateDto requestStatusUpdateDto, Long userId, Long eventId) {
        List<Request> requestsToUpdate = requestRepository.findRequestsByIdIn(requestStatusUpdateDto.getRequestIds());
        RequestStatusResultDto result = new RequestStatusResultDto();
        List<Request> confirmedReq = new ArrayList<>();
        List<Request> rejectedReq = new ArrayList<>();
        Event event = getEvent(eventId);
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            result.setConfirmedRequests(requestMapper.toRequestDtoList(confirmedReq));
            result.setRejectedRequests(requestMapper.toRequestDtoList(rejectedReq));
            log.info("confirmation of requests is not required");
            return result;
        }
        if (requestStatusUpdateDto.getStatus().equals(RequestStatusToUpdate.CONFIRMED)) {
            for (int i = 0; i < requestsToUpdate.size(); i++) {
                if (!requestsToUpdate.get(i).getStatus().equals(RequestStatus.PENDING)) {
                    throw new RequestStatusException("request " + requestsToUpdate.get(i).getId() + " wasn't PENDING");
                }
                if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
                    System.out.println(event.getConfirmedRequests());
                        requestsToUpdate.get(i).setStatus(RequestStatus.REJECTED);
                        rejectedReq.add(requestsToUpdate.get(i));
                        requestRepository.save(requestsToUpdate.get(i));
                        throw new ParticipantLimitException();
                } else {
                    requestsToUpdate.get(i).setStatus(RequestStatus.CONFIRMED);
                    confirmedReq.add(requestsToUpdate.get(i));
                    requestRepository.save(requestsToUpdate.get(i));
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    eventRepository.save(event);
                }
            }
        } else {
            requestsToUpdate.stream()
                .filter(x -> x.getStatus().equals(RequestStatus.CONFIRMED))
                .findAny()
                .ifPresent(x -> {
                    throw new RequestStatusException("request " + x.getId() + " is confirmed");
                });

            rejectedReq = requestsToUpdate.stream()
                .peek(x -> x.setStatus(RequestStatus.REJECTED))
                .collect(Collectors.toList());
        }

        result.setRejectedRequests(rejectedReq.stream()
            .map(requestMapper::toDto)
            .collect(Collectors.toList()));

        result.setConfirmedRequests(confirmedReq.stream()
            .map(requestMapper::toDto)
            .collect(Collectors.toList()));

        log.info("requests status was changed");
        return result;
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    private Request getRequest(Long requestId) {
        return requestRepository.findById(requestId)
            .orElseThrow(() -> new RequestNotFoundException(requestId));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
