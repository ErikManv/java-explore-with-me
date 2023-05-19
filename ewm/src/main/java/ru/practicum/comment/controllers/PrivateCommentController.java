package ru.practicum.comment.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentDtoIn;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}")
@RequiredArgsConstructor
public class PrivateCommentController {

    private final CommentService  commentServiceImpl;

    @PostMapping("events/{eventId}/comments")
    public ResponseEntity<CommentDto> addComment(@PathVariable("eventId") Long eventId,
                                                 @PathVariable("userId") Long userId,
                                                 @Valid @RequestBody CommentDtoIn commentDtoIn) {
        return new ResponseEntity<>(commentServiceImpl.addComment(commentDtoIn, eventId, userId), HttpStatus.CREATED);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId,
                                              @PathVariable("userId") Long authorId) {
        commentServiceImpl.deleteComment(commentId, authorId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable("commentId") Long commentId,
                                                    @PathVariable("userId") Long authorId,
                                                    @RequestBody CommentDtoIn commentDtoIn) {
        return new ResponseEntity<>(commentServiceImpl.updateComment(commentDtoIn, commentId, authorId), HttpStatus.OK);
    }

    @GetMapping("/comments")
    public ResponseEntity<List<CommentDto>> getOwnComments(@PathVariable("userId") Long authorId,
                                                           @RequestParam(value = "from", defaultValue = "0", required = false)
                                                           Integer from,
                                                           @RequestParam(value = "size", defaultValue = "10", required = false)
                                                               Integer size) {
        return new ResponseEntity<>(commentServiceImpl.getOwnComments(authorId, from, size), HttpStatus.OK);
    }
}
