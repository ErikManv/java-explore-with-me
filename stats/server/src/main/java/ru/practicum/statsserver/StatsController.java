package ru.practicum.statsserver;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
    public void save(@RequestBody HitDtoInput hitDtoIn) {
        statsRepository.save(hitMapper.toModel(hitDtoIn));
    }

    @GetMapping("/stats")
    public List<HitDtoOut> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(defaultValue = "false",
                                        required = false) boolean unique) {
        if (unique) {
            if (uris == null) {
                return statsRepository.getStatisticsUniq(start, end);
            } else {
                return statsRepository.getStatisticsUniqAndUris(start, end, uris);
            }
        } else {
            if (uris == null) {
                return statsRepository.getStatistics(start, end);
            } else {
                return statsRepository.getStatisticsUris(start, end, uris);
            }
        }
    }
}
