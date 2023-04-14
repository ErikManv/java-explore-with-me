package ru.practicum.statsdto;

import lombok.Data;

@Data
public class HitDtoOut {
    public final String app;
    public final String uri;
    public final Long hits;

    public HitDtoOut(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}