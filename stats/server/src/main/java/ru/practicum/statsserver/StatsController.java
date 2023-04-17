package ru.practicum.statsserver;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statsdto.HitDtoInput;
import ru.practicum.statsdto.HitDtoOut;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsRepository statsRepository;
    private final HitMapper hitMapper;

    @PostMapping("/hit")
    public void hit(@RequestBody HitDtoInput hitDtoInput) {
        statsRepository.save(hitMapper.toHit(hitDtoInput));
    }

    @GetMapping("/stats")
    public ResponseEntity<List<HitDtoOut>> stats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                                @RequestParam(required = false) List<String> uris,
                                                @RequestParam(defaultValue = "false", required = false) boolean unique) {
        if (unique) {
            if (uris == null) {
                return new ResponseEntity<>(statsRepository.getStatisticsUniq(start, end), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(statsRepository.getStatisticsUniqAndUris(start, end, uris), HttpStatus.OK);
            }
        } else {
            if (uris == null) {
                return new ResponseEntity<>(statsRepository.getStatistics(start, end), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(statsRepository.getStatisticsUris(start, end, uris), HttpStatus.OK);
            }
        }
    }
}





