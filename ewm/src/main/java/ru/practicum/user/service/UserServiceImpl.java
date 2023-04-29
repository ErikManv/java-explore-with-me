package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.user.controllers.AdminUserController;
import ru.practicum.exceptions.DuplicateException;
import ru.practicum.exceptions.notFound.UserNotFoundException;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserDtoIn;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private static final Logger log = LoggerFactory.getLogger(AdminUserController.class);

    @Override
    public List<UserDto> findUsers(List<Long> ids, Integer from, Integer size) {
        List<User> userList;

        if (ids != null) {
            log.info("returned list of users by ids");
            userList = userRepository.findUsers(ids);
        } else {
            log.info("returned limited list of users");
            userList = userRepository.findAll(PageRequest.of(from, size)).toList();
        }
        return userList.stream()
            .map(userMapper::toUserDto)
            .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDtoIn userDtoIn) {
        if (userRepository.existsByName(userDtoIn.getName())) {
            throw new DuplicateException("user", userDtoIn.toString());
        }
        User user = userMapper.toUser(userDtoIn);
        log.info("user {} created", userDtoIn.getName());
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void delete(Long userId) {
        User user = getUser(userId);
        userRepository.delete(user);
        log.info("user {} deleted", user.getName());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
