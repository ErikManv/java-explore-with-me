package ru.practicum.category.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.category.model.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "SELECT *" +
    "FROM categories " +
    "OFFSET :from " +
    "LIMIT :size ", nativeQuery = true)
    List<Category> findAll(@Param("from") Integer from,
                           @Param("size") Integer size);


    Boolean existsByName(String name);

}
