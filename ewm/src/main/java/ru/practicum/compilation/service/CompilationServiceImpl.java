package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.dao.CompilationRepository;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationDtoIn;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.controllers.AdminCompilationController;
import ru.practicum.event.model.Event;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.exceptions.notFound.CompilationNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    private final CompilationMapper compilationMapper;

    private static final Logger log = LoggerFactory.getLogger(AdminCompilationController.class);

    @Override
    public CompilationDto addCompilation(CompilationDtoIn compilationDtoIn) {
        List<Event> events = eventRepository.findEventsByIdIn(compilationDtoIn.getEvents());
        Compilation compilation = Compilation.builder()
            .pinned(compilationDtoIn.isPinned())
            .title(compilationDtoIn.getTitle())
            .events(events)
            .build();
        log.info("compilation {} created", compilationDtoIn.getTitle());
        return compilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilation(Long compId) {
        getCompilation(compId);
        compilationRepository.deleteById(compId);
        log.info("compilation {} deleted", compId);
    }


    @Override
    public CompilationDto getCompilationById(Long compId) {
        log.info("compilation {} found", compId);
        System.out.println(getCompilation(compId));
        return compilationMapper.toDto(getCompilation(compId));
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        return compilationRepository.findComps(pinned, from, size).stream()
            .map(compilationMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public CompilationDto updateCompilation(CompilationDtoIn compilationDtoIn, Long compId) {
        Compilation compilation = getCompilation(compId);
        Compilation result = Compilation.builder()
            .id(compilation.getId())
            .title(compilationDtoIn.getTitle() != null ? compilationDtoIn.getTitle() : compilation.getTitle())
            .pinned(compilationDtoIn.isPinned() != compilation.isPinned() ? compilationDtoIn.isPinned() : compilation.isPinned())
            .build();
        List<Event> events = eventRepository.findEventsByIdIn(compilationDtoIn.getEvents());
        result.setEvents(events);
        log.info("compilation {} updated", compId);
        return compilationMapper.toDto(compilationRepository.save(result));
    }

    private Compilation getCompilation(Long compId) {
        return compilationRepository.findById(compId)
            .orElseThrow(() -> new CompilationNotFoundException(compId));
    }

}
