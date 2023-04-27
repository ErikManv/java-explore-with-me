package ru.practicum.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestStatusResultDto;
import ru.practicum.request.dto.RequestStatusUpdateDto;

import java.util.List;

@Service
public interface RequestService {

    List<RequestDto> getUserRequests(Long userId);

    RequestDto addRequest(Long eventId, Long userId);

    RequestDto cancelRequest(Long userId, Long eventId);

    List<RequestDto> getEventParticipants(Long userId, Long eventId);

    RequestStatusResultDto changeRequestStatus(RequestStatusUpdateDto requestStatusUpdateDto, Long userId, Long eventId);
}
