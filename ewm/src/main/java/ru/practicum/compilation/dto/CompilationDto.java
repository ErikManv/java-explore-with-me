package ru.practicum.compilation.dto;

import lombok.*;
import ru.practicum.event.dto.EventDtoShort;

import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {

    private Long id;

    private List<EventDtoShort> events;

    @NotNull
    private boolean pinned;

    private String title;
}
