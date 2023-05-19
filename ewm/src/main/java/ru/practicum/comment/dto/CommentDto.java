package ru.practicum.comment.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;

    @NotEmpty
    private String text;

    private Long eventId;

    private Long authorId;

    private LocalDateTime created;
}
