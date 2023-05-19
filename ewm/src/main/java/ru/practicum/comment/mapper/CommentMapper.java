package ru.practicum.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentDtoIn;
import ru.practicum.comment.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "eventId", source = "event.id")
    CommentDto toDto(Comment comment);

    Comment toModel(CommentDtoIn commentDtoIn);
}
