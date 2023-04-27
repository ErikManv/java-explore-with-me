package ru.practicum.exceptions.notFound;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long e) {
        super(String.format("Category %s not found", e));
    }
}