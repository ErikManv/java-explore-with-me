package ru.practicum.comment.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDtoIn {
    @NotNull
    private String text;

}