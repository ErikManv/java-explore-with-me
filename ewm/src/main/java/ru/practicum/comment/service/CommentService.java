package ru.practicum.comment.service;

import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentDtoIn;
import ru.practicum.enums.SortValuesComment;

import java.util.List;

@Service
public interface CommentService {

    CommentDto addComment(CommentDtoIn commentDtoIn, Long eventId, Long authorId);

    CommentDto updateComment(CommentDtoIn commentDtoIn,Long commentId, Long authorId);

    CommentDto getComment(Long commentId, Long eventId);

    void deleteComment(Long commentId, Long userId);

    void deleteCommentAdmin(Long commendId);

    List<CommentDto> getOwnComments(Long authorId, Integer from, Integer size);

    List<CommentDto> getCommentsByEvent(Long eventId, Integer from, Integer size, SortValuesComment sort);
}
