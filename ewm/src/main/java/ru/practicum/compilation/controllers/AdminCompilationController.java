package ru.practicum.compilation.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationDtoIn;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> addCompilation(@Valid @RequestBody CompilationDtoIn compilationDtoIn) {
        return new ResponseEntity<>(compilationService.addCompilation(compilationDtoIn), HttpStatus.CREATED);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<CompilationDto> deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable Long compId,
                                                            @RequestBody CompilationDtoIn compilationDtoIn) {
        return new ResponseEntity<>(compilationService.updateCompilation(compilationDtoIn, compId), HttpStatus.OK);
    }
}
