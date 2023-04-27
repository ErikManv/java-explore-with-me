package ru.practicum.request.model;

import lombok.*;
import ru.practicum.enums.RequestStatus;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime created;

    @OneToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;

    @OneToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    private User requester;
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
