package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.comment.controllers.AdminCommentController;
import ru.practicum.comment.controllers.PrivateCommentController;
import ru.practicum.comment.controllers.PublicCommentsController;
import ru.practicum.comment.dao.CommentRepository;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentDtoIn;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.enums.SortValuesComment;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exceptions.PermissionException;
import ru.practicum.exceptions.notFound.CommentNotFoundException;
import ru.practicum.exceptions.notFound.EventNotFoundException;
import ru.practicum.exceptions.notFound.UserNotFoundException;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private static final Logger logAdmin = LoggerFactory.getLogger(AdminCommentController.class);
    private static final Logger logPrivate = LoggerFactory.getLogger(PrivateCommentController.class);
    private static final Logger logPublic = LoggerFactory.getLogger(PublicCommentsController.class);

    @Override
    public CommentDto addComment(CommentDtoIn commentDtoIn, Long eventId, Long authorId) {
        Event event = getEvent(eventId);
        User author = getUser(authorId);
        Comment comment = Comment.builder()
            .text(commentDtoIn.getText())
            .author(author)
            .event(event)
            .created(LocalDateTime.now())
            .build();

        logPrivate.info("Comment {} created by user ", authorId);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateComment(CommentDtoIn commentDtoIn, Long commentId, Long authorId) {
        Comment comment = getCommentById(commentId);
        getUser(authorId);
        if (comment.getAuthor().getId().equals(authorId)) {
            comment.setText(commentDtoIn.getText());
            comment.setCreated(LocalDateTime.now());
            logPrivate.info("Comment {} updated", commentId);
            return commentMapper.toDto(commentRepository.save(comment));
        } else {
            throw new PermissionException(authorId);
        }
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = getCommentById(commentId);
        if (comment.getAuthor().getId().equals(userId)) {
            commentRepository.deleteById(commentId);
            logPrivate.info("Comment {} deleted", commentId);
        } else {
            throw new PermissionException(userId);
        }
    }

    @Override
    public List<CommentDto> getOwnComments(Long authorId, Integer from, Integer size) {
        getUser(authorId);
        logPrivate.info("list of user's {} comments returned", authorId);
        return commentRepository.findCommentsByAuthorId(authorId, PageRequest.of(from, size)).stream()
            .map(commentMapper::toDto)
            .collect(Collectors.toList());
    }

    /////////////////////////////////////////
    @Override
    public void deleteCommentAdmin(Long commentId) {
        getCommentById(commentId);
        commentRepository.deleteById(commentId);
        logAdmin.info("Comment {} deleted by admin", commentId);
    }

    ///////////////////////////////////////////
    @Override
    public List<CommentDto> getCommentsByEvent(Long eventId, Integer from, Integer size, SortValuesComment sort) {
        getEvent(eventId);
        List<Comment> result;
        if (sort.equals(SortValuesComment.ASC)) {
            result = commentRepository.findCommentsByEventId(eventId,
                    PageRequest.of(from, size, Sort.by("created").ascending())).toList();
            logPublic.info("Comment list returned sort by {}", sort);
        } else {
            result = commentRepository.findCommentsByEventId(eventId,
                PageRequest.of(from, size, Sort.by("created").descending())).toList();
            logPublic.info("Comment list returned sort by {}", sort);
        }
        return result.stream()
            .map(commentMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public CommentDto getComment(Long commentId, Long eventId) {
        getEvent(eventId);
        logAdmin.info("Comment {} returned", commentId);
        return commentMapper.toDto(getCommentById(commentId));
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException(eventId));
    }
}
