package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
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

    @Override
    public List<RequestDto> getUserRequests(Long userId) {
        getUser(userId);
        return requestMapper.toRequestDtoList(requestRepository.findRequestsByRequesterId(userId));
    }

    @Override
    public RequestDto addRequest(Long eventId, Long userId) {
        Event event = getEvent(eventId);
        if (userId.equals(event.getInitiator().getId())) {
            throw new UserRequestOwnEventException("нельзя создавать запрос на собственный Event");
        }

        if (requestRepository.findRequestByEventAndRequester(getEvent(eventId), getUser(userId)) != null) {
            throw new UserRequestOwnEventException("нельзя создавать потвторный запрос");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new IllegalEventStateException("данное событие не PUBLISHED");
        }

        List<Request> requests = requestRepository.findAllByEventId(eventId);

        if (!event.getRequestModeration() && requests.size() >= event.getParticipantLimit()) {
            throw new IllegalEventStateException("нет мест");
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
        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        Request request = getRequest(requestId);
        request.setStatus(RequestStatus.CANCELED);
        return  requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getEventParticipants(Long userId, Long eventId) {
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
            return result;
        }
        if (requestStatusUpdateDto.getStatus().equals(RequestStatusToUpdate.CONFIRMED)) {
            for (int i = 0; i < requestsToUpdate.size(); i++) {
                if (!requestsToUpdate.get(i).getStatus().equals(RequestStatus.PENDING)) {
                    throw new StateException("request " + requestsToUpdate.get(i).getId() + " wasn't PENDING");
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
                    throw new StateException("request " + x.getId() + " is confirmed");
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
