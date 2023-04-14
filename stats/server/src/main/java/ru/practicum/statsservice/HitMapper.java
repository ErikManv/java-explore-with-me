package ru.practicum.statsservice;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.statsdto.HitDtoInput;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface HitMapper {
    @Mapping(source = "timestamp",target = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Hit toModel(HitDtoInput hitDtoInput);
}