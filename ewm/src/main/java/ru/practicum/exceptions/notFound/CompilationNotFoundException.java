package ru.practicum.exceptions.notFound;

public class CompilationNotFoundException extends NotFoundException {

    public CompilationNotFoundException(Long e) {
        super(String.format("Compilation %s not found", e));
    }
}