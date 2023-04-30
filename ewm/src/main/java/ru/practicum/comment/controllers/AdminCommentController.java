package ru.practicum.comment.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.service.CommentService;

@RestController
@RequestMapping(path = "/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentServiceImpl;

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteCommentAdmin(@PathVariable("commentId") Long commentId) {
        commentServiceImpl.deleteCommentAdmin(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
