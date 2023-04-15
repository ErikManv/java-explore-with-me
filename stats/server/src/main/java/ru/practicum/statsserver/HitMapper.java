package ru.practicum.statsserver;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.statsdto.HitDtoInput;

@Mapper(componentModel = "spring")
public interface HitMapper {
    @Mapping(source = "timestamp",target = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Hit toHit(HitDtoInput hitDtoInput);
}