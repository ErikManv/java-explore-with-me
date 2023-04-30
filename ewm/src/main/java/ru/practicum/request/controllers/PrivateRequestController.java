package ru.practicum.request.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.service.RequestService;
import ru.practicum.request.dto.RequestStatusResultDto;
import ru.practicum.request.dto.RequestStatusUpdateDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class PrivateRequestController {

    private final RequestService requestServiceImpl;

    @PostMapping("/{userId}/requests")
    public ResponseEntity<RequestDto> addRequest(@PathVariable Long userId,
                                                 @RequestParam Long eventId) {
        return new ResponseEntity<>(requestServiceImpl.addRequest(eventId, userId), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<List<RequestDto>> getUserRequests(@PathVariable Long userId) {
        return new ResponseEntity<>(requestServiceImpl.getUserRequests(userId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<RequestDto> cancelRequest(@PathVariable Long userId,
                                              @PathVariable Long requestId) {
        return new ResponseEntity<>(requestServiceImpl.cancelRequest(userId, requestId), HttpStatus.OK);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<RequestDto>> getEventParticipants(@PathVariable Long userId,
                                                                  @PathVariable Long eventId) {
        return new ResponseEntity<>(requestServiceImpl.getEventParticipants(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<RequestStatusResultDto> changeRequestStatus(@PathVariable Long userId,
                                                                      @PathVariable Long eventId,
                                                                      @RequestBody RequestStatusUpdateDto requestStatusUpdateDto) {
        return new ResponseEntity<>(requestServiceImpl.changeRequestStatus(requestStatusUpdateDto, userId, eventId), HttpStatus.OK);
    }
}
