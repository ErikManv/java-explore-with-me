package ru.practicum.user.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserDtoIn;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userServiceImpl;

    @GetMapping
    public ResponseEntity<List<UserDto>> findUsers(@RequestParam(required = false, name = "ids") List<Long> ids,
                                                   @RequestParam(value = "from", defaultValue = "0", required = false)
                                                     Integer from,
                                                   @RequestParam(value = "size", defaultValue = "10", required = false)
                                                     Integer size) {
        return new ResponseEntity<>(userServiceImpl.findUsers(ids, from, size), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDtoIn userDtoIn) {
        return new ResponseEntity<>(userServiceImpl.createUser(userDtoIn), HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable("userId") Long userId) {
        userServiceImpl.delete(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
