package ru.practicum.event.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.category.model.Category;
import ru.practicum.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e " +
    "FROM Event e " +
    "WHERE (e.initiator = :user) ")
    List<Event> findEventsByInitiator(@Param("user") User user, Pageable pageable);

    List<Event> findEventsByIdIn(List<Long> eventIds);

    Event findEventByInitiatorIdAndId(Long userId, Long eventId);

    Boolean existsByCategory(Category category);

    @Query("SELECT e " +
        "FROM Event e " +
        "WHERE (e.initiator IN :users OR :users IS NULL) " +
        "AND (e.category IN :categories  OR :categories IS NULL) " +
        "AND (e.eventDate >= to_timestamp(:rangeStart, 'yyyy-mm-dd hh24:mi:ss')  OR to_timestamp(:rangeStart, 'yyyy-mm-dd hh24:mi:ss') IS NULL) " +
        "AND (e.eventDate <= to_timestamp(:rangeEnd, 'yyyy-mm-dd hh24:mi:ss')   OR to_timestamp(:rangeEnd, 'yyyy-mm-dd hh24:mi:ss') IS NULL) ")
    List<Event> searchWithoutStateByAdmin(@Param("users") List<User> users,
                                          @Param("categories") List<Category> categories,
                                          @Param("rangeStart") LocalDateTime rangeStart,
                                          @Param("rangeEnd") LocalDateTime rangeEnd, Pageable pageable);

    @Query("SELECT e " +
        "FROM Event e " +
        "WHERE (e.initiator IN :users OR :users IS NULL) " +
        "AND e.state IN :states " +
        "AND (e.category IN :categories  OR :categories IS NULL) " +
        "AND (e.eventDate >= to_timestamp(:rangeStart, 'yyyy-mm-dd hh24:mi:ss')  OR to_timestamp(:rangeStart, 'yyyy-mm-dd hh24:mi:ss') IS NULL) " +
        "AND (e.eventDate <= to_timestamp(:rangeEnd, 'yyyy-mm-dd hh24:mi:ss')   OR to_timestamp(:rangeEnd, 'yyyy-mm-dd hh24:mi:ss') IS NULL) ")
    List<Event> searchByAdmin(@Param("users") List<User> users,
                              @Param("states") List<EventState> states,
                              @Param("categories") List<Category> categories,
                              @Param("rangeStart") LocalDateTime rangeStart,
                              @Param("rangeEnd") LocalDateTime rangeEnd, Pageable pageable);

    @Query("SELECT e " +
        "FROM Event e " +
        "WHERE (lower(e.annotation) LIKE '%'||lower(:text)||'%' OR lower(e.description) LIKE '%'||lower(:text)||'%') " +
        "AND (e.category IN :categories  OR :categories IS NULL) " +
        "AND (:paid IS NULL OR e.paid = :paid) " +
        "AND (e.eventDate BETWEEN " +
        "to_timestamp(:rangeStart, 'yyyy-mm-dd hh24:mi:ss') AND to_timestamp(:rangeEnd, 'yyyy-mm-dd hh24:mi:ss') " +
        "OR e.eventDate > CURRENT_TIMESTAMP) " )
    List<Event> searchPublic(@Param("text") String text,
                             @Param("categories") List<Category> categories,
                             @Param("paid") Boolean paid,
                             @Param("rangeStart") LocalDateTime rangeStart,
                             @Param("rangeEnd") LocalDateTime rangeEnd,
                             Pageable pageable);
}
