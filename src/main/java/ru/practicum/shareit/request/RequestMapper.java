package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestForOwnerDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static RequestDto toDto(Request request) {
        if (request == null) {
            return null;
        }

        String createdDate = DATE_TIME_FORMATTER.format(LocalDateTime.now());

        return RequestDto.builder()
                .id(request.getId())
                .requestorId(request.getRequestUser() != null ? request.getRequestUser().getId() : null)
                .description(request.getDescription())
                .createdDate(createdDate)
                .build();
    }

    public static RequestForOwnerDto toEntityForOwnerDto(Request request) {
        if (request == null) {
            return null;
        }

        String createdDate = DATE_TIME_FORMATTER.format(request.getCreatedDate());

        return RequestForOwnerDto.builder()
                .id(request.getId())
                .requestorId(request.getRequestUser() != null ? request.getRequestUser().getId() : null)
                .description(request.getDescription())
                .createdDate(createdDate)
                .items(null)
                .build();
    }

public static RequestWithItemsDto toEntityWithItemsDto(Request request) {
        if (request == null) {
            return null;
        }

        String createdDate = DATE_TIME_FORMATTER.format(request.getCreatedDate());

        return RequestWithItemsDto.builder()
                .id(request.getId())
                .requestorId(request.getRequestUser() != null ? request.getRequestUser().getId() : null)
                .description(request.getDescription())
                .createdDate(createdDate)
                .items(null)
                .build();
    }

    public static List<RequestDto> toDto(List<Request> requests) {
        return requests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }


    public static Request toEntity(RequestDto dto, User requestUser) {
        if (dto == null) {
            return null;
        }

        return Request.builder()
                .id(dto.getId())
                .requestUser(requestUser)
                .description(dto.getDescription())
                .createdDate(LocalDateTime.now())
                .build();
    }

}
