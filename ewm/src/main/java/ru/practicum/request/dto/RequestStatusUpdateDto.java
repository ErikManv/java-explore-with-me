package ru.practicum.request.dto;

import lombok.*;
import ru.practicum.enums.RequestStatus;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestStatusUpdateDto {
    private List<Long> requestIds;
    private RequestStatus status;
}
