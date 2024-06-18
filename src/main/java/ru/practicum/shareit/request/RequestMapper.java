package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {

    public static RequestDto toRequestDto(Request request) {
        if (request == null) {
            return null;
        }

        return RequestDto.builder()
                .id(request.getId())
                .requestorId(request.getRequestUser() != null ? request.getRequestUser().getId() : null)
                .description(request.getDescription())
                .build();
    }

    public static List<RequestDto> toRequestDto(List<Request> requests) {
        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }


    public static Request toRequest(RequestDto dto, User requestUser) {
        if (dto == null) {
            return null;
        }

        return Request.builder()
                .id(dto.getId())
                .requestUser(requestUser)
                .description(dto.getDescription())
                .build();
    }

}
