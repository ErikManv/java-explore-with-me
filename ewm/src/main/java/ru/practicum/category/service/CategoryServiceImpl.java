package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.dao.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.category.controllers.AdminCategoryController;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.exceptions.notFound.CategoryNotFoundException;
import ru.practicum.exceptions.DeleteObjectInUseException;
import ru.practicum.exceptions.DuplicateException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    private static final Logger log = LoggerFactory.getLogger(AdminCategoryController.class);


    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new DuplicateException("category", categoryDto.getName());
        }
        Category category = categoryMapper.toModel(categoryDto);
        log.info("category {} created", categoryDto.getName());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new DuplicateException("category", categoryDto.getName());
        }
        Category category = getCategory(categoryId);
        category.setName(categoryDto.getName());
        log.info("category {} updated", categoryDto.getName());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long categoryId) {
        if (eventRepository.existsByCategory(getCategory(categoryId))) {
            throw new DeleteObjectInUseException("category", categoryId.toString());
        }
        getCategory(categoryId);
        categoryRepository.deleteById(categoryId);
        log.info("category {} deleted", categoryId);
    }

    @Override
    public List<CategoryDto> findAll(Integer from, Integer size) {
        log.info("categories list returned");
        return categoryRepository.findAll(from, size).stream().map(categoryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(Long categoryId) {
        Category category = getCategory(categoryId);
        log.info("category {} found", categoryId);
        return categoryMapper.toDto(category);
    }


    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }
}
