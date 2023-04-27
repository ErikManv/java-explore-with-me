package ru.practicum.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    private String message;
    private String reason;
    private String status;
    private String timestamp;
}
