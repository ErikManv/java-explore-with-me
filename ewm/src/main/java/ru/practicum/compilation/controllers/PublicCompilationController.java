package ru.practicum.compilation.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getCompilations(@RequestParam(required = false, name = "pinned") Boolean pinned,
                                                                @RequestParam(value = "from", defaultValue = "0", required = false)
                                                                Integer from,
                                                                @RequestParam(value = "size", defaultValue = "10", required = false)
                                                                    Integer size) {
        return new ResponseEntity<>(compilationService.getCompilations(pinned, from, size), HttpStatus.OK);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilation(@PathVariable Long compId) {
        return new ResponseEntity<>(compilationService.getCompilationById(compId), HttpStatus.OK);
    }
}
