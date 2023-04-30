package ru.practicum.request.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestStatusResultDto {
    List<RequestDto> confirmedRequests;
    List<RequestDto> rejectedRequests;
}
