package ru.practicum.user.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.user.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByName(String name);

    @Query(value =  "SELECT u " +
        "FROM users u " +
        "OFFSET :from " +
        "LIMIT :size", nativeQuery = true)
    List<User> findUsersFrom(@Param("from") Integer from,
                             @Param("size") Integer size);

    @Query(value =  "SELECT u " +
        "FROM User u " +
        "WHERE u.id IN :ids ")
    List<User> findAllById(List<Long> ids);

}
