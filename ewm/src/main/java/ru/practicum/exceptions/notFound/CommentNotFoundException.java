package ru.practicum.exceptions.notFound;

public class CommentNotFoundException extends NotFoundException {

    public CommentNotFoundException(Long e) {
        super(String.format("Comment %s not found", e));
    }
}
