package ru.practicum.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder
@Data
public class UserDtoIn {

    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}
