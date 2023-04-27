package ru.practicum.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Request;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(source = "event", target = "event.id")
    @Mapping(source = "requester", target = "requester.id")
    Request toModel(RequestDto requestDto);

    @Mapping(source = "event.id", target = "event")
    @Mapping(source = "requester.id", target = "requester")
    RequestDto toDto(Request request);

    List<RequestDto> toRequestDtoList(List<Request> requests);

}
