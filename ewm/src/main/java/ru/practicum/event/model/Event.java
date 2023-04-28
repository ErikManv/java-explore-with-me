package ru.practicum.event.model;

import lombok.*;
import ru.practicum.enums.EventState;
import ru.practicum.category.model.Category;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String annotation;
    @OneToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
    private int confirmedRequests;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    private String description;
    private LocalDateTime eventDate;
    @OneToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    private User initiator;
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;
    private boolean paid;
    @Column(name = "participant_limit")
    private Long participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    @Builder.Default
    private Boolean requestModeration = true;
    @Enumerated(EnumType.STRING)
    private EventState state;
    private String title;
    private Long views;
}
