package ru.practicum.statsdto;

import lombok.Data;

@Data
public class HitDtoOut {
    private String app;
    private String uri;
    private Long hits;

    public HitDtoOut(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}
