package ru.practicum.category.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;

@Mapper(componentModel = "spring")
    public interface CategoryMapper {

        CategoryDto toDto(Category category);

        @InheritInverseConfiguration
        Category toModel(CategoryDto userDto);
    }
