package ru.practicum.user.dto;

import lombok.*;

@Builder
@Data
public class UserDto {

    private Long id;
    private String name;
    private String email;
}
