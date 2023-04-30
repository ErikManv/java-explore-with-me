package ru.practicum.compilation.service;

import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationDtoIn;

import java.util.List;

@Service
public interface CompilationService {

    CompilationDto addCompilation(CompilationDtoIn compilationDtoIn);

    void deleteCompilation(Long compilationId);

    CompilationDto updateCompilation(CompilationDtoIn compilationDtoIn, Long compId);

    CompilationDto getCompilationById(Long compId);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);
}
