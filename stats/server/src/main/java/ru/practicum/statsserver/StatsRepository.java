package ru.practicum.statsserver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.statsdto.HitDtoOut;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Long> {

    @Query("SELECT new ru.practicum.statsdto.HitDtoOut(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.ip, h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<HitDtoOut> getStatisticsUniq(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.statsdto.HitDtoOut(h.app, h.uri, COUNT(h.ip)) " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.ip, h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<HitDtoOut> getStatistics(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.statsdto.HitDtoOut(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit h " +
            "WHERE h.uri IN :uris " +
            "AND h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.ip, h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<HitDtoOut> getStatisticsUniqAndUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.statsdto.HitDtoOut(h.app, h.uri, COUNT(h.ip)) " +
            "FROM Hit h " +
            "WHERE h.uri IN :uris " +
            "AND h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.ip, h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<HitDtoOut> getStatisticsUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
