package ru.practicum.comment.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findCommentsByEventId(Long eventId, Pageable pageable);

    Page<Comment> findCommentsByAuthorId(Long authorId, Pageable pageable);
}
