package ru.practicum.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDtoIn {

        private List<Long> events;
        private boolean pinned;
        @NotBlank
        private String title;
}
