package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.event.dto.EventDto;
import ru.practicum.event.dto.EventDtoIn;
import ru.practicum.event.model.Event;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface EventMapper {
    EventDto toEventDto(Event event);

    @Mapping(source = "category", target = "category.id")
    Event toEventModel(EventDtoIn newEventDto);

    List<EventDto> toEventDtoList(List<Event> events);
}
