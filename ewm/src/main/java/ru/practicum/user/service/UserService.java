package ru.practicum.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserDtoIn;

import java.util.List;

@Service
public interface UserService {

    List<UserDto> findUsers(List<Long> ids, Integer from, Integer size);

    UserDto createUser(UserDtoIn user);

    void delete(Long userId);

}
