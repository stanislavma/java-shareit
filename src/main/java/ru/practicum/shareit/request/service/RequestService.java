package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;

public interface RequestService {

    RequestDto add(RequestDto requestDto, Long userId);

}