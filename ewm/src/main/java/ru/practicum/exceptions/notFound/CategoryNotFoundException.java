package ru.practicum.exceptions.notFound;

public class CategoryNotFoundException extends NotFoundException {

    public CategoryNotFoundException(Long e) {
        super(String.format("Category %s not found", e));
    }
}