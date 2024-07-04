package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestForOwnerDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;

import java.util.List;

public interface RequestService {

    RequestDto add(RequestDto requestDto, Long userId);

    List<RequestForOwnerDto> getAllByOwnerId(Long userId);

    List<RequestWithItemsDto> getAllByUserIdAndPageable(Long userId, Integer from, Integer size);

    RequestWithItemsDto getRequestById(Long userId, Long requestId);

}