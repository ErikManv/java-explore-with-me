package ru.practicum.category.service;

import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;

import java.util.List;

@Service
public interface CategoryService {
    List<CategoryDto> findAll(Integer from, Integer size);

    CategoryDto getById(Long categoryId);

    CategoryDto addCategory(CategoryDto categoryDto);

    void deleteCategory(Long categoryId);

    CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId);
}
