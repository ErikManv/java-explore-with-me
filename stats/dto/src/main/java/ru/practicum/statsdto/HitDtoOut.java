package ru.practicum.statsdto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HitDtoOut {
    private String app;
    private String uri;
    private Long hits;
}
