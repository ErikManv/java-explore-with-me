package ru.practicum.compilation.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.compilation.model.Compilation;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query(value = "SELECT * " +
        "FROM compilations " +
        "WHERE (is_pinned = :pinned OR :pinned IS NULL) " +
        "OFFSET :from " +
        "LIMIT :size", nativeQuery = true)
    List<Compilation> findComps(@Param("pinned") Boolean pinned,
                                @Param("from") Integer from,
                                @Param("size") Integer size);
}
