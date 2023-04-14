package ru.practicum.statsdto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class HitDtoInput {
    @NotEmpty
    public String app;
    @NotEmpty
    public String uri;
    @NotEmpty
    public String ip;
    @NotEmpty
    public String timestamp;
}