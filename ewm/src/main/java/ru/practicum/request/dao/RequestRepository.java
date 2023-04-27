package ru.practicum.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.Request;
import ru.practicum.user.model.User;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findRequestsByRequesterId(Long userId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findRequestsByIdIn(List<Long> requests);

    Request findRequestByEventAndRequester(Event event, User user);

    @Query("SELECT r from Request AS r " +
    "JOIN Event AS e ON r.event.id = e.id " +
    "WHERE r.event.id = :eventId AND e.initiator.id = :userId")
    List<Request> getEventParticipants(Long userId, Long eventId);
}
