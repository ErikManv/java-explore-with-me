package ru.practicum.user.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.user.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findUsersByIdIn(List<Long> ids);

    @Query("SELECT u FROM User u WHERE u.id IN :ids ")
    List<User> findUsers(List<Long> ids);

    Page<User> findAll(Pageable pageable);

    boolean existsByName(String name);

}
