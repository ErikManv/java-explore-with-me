package ru.practicum.comment.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;
import ru.practicum.enums.SortValuesComment;

import java.util.List;

@RestController
@RequestMapping(path = "/event/{eventId}/comments")
@RequiredArgsConstructor
public class PublicCommentsController {

    private final CommentService commentServiceImpl;

    @GetMapping
    public ResponseEntity<List<CommentDto>> getCommentsByEvent(@PathVariable("eventId") Long eventId,
                                                               @RequestParam(value = "from", defaultValue = "0", required = false)
                                                               Integer from,
                                                               @RequestParam(value = "size", defaultValue = "10", required = false)
                                                               Integer size,
                                                               @RequestParam(value = "sort", defaultValue = "DESC", required = false)
                                                               SortValuesComment sort) {
        return new ResponseEntity<>(commentServiceImpl.getCommentsByEvent(eventId, from, size, sort), HttpStatus.OK);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getComment(@PathVariable("eventId") Long eventId,
                                                 @PathVariable("commentId") Long commentId) {
        return new ResponseEntity<>(commentServiceImpl.getComment(commentId, eventId), HttpStatus.OK);
    }
}
