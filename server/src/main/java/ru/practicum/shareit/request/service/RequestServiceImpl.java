package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestForOwnerDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final ItemService itemService;

    private final Sort requestsSort = Sort.by(Sort.Direction.ASC, "createdDate");

    @Override
    @Transactional()
    public RequestDto add(RequestDto requestDto, Long userId) {
        User user = findUserById(userId);

        Request request = RequestMapper.toEntity(requestDto, user);
        request.setRequestUser(user);

        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestForOwnerDto> getAllByOwnerId(Long userId) {
        findUserById(userId);

        List<Request> requests = requestRepository.findAllByRequestUserId(userId, requestsSort);

        List<RequestForOwnerDto> requestForOwnerDtoList = new ArrayList<>();

        for (Request request : requests) {
            RequestForOwnerDto requestForOwnerDto = RequestMapper.toEntityForOwnerDto(request);

            if (request.getRequestUser().getId().equals(userId)) {
                requestForOwnerDto.setItems(getItemsByRequestId(request.getId()));
            }

            requestForOwnerDtoList.add(requestForOwnerDto);
        }

        return requestForOwnerDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestWithItemsDto> getAllByUserIdAndPageable(Long userId, Integer from, Integer size) {
        findUserById(userId);
        validatePageable(from, size);

        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, requestsSort);

        List<Request> requests = requestRepository
                .findAllByRequestUserIdNot(userId, pageable)
                .getContent();

        List<RequestWithItemsDto> requestWithItemsDtoList = new ArrayList<>();

        for (Request request : requests) {
            RequestWithItemsDto requestWithItemsDto = RequestMapper.toEntityWithItemsDto(request);
            requestWithItemsDto.setItems(getItemsByRequestId(request.getId()));
            requestWithItemsDtoList.add(requestWithItemsDto);
        }

        return requestWithItemsDtoList;
    }

    public RequestWithItemsDto getRequestById(Long userId, Long requestId) {
        findUserById(userId);

        Request request = getRequestById(requestId);

        RequestWithItemsDto requestWithItemsDto = RequestMapper.toEntityWithItemsDto(request);
        requestWithItemsDto.setItems(getItemsByRequestId(request.getId()));

        return requestWithItemsDto;
    }

    private Request getRequestById(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    String errorText = "Запрос на вещь не найден: " + requestId;
                    log.error(errorText);
                    return new EntityNotFoundException(errorText);
                });
    }

    private List<ItemDto> getItemsByRequestId(Long requestId) {
        return itemService.getItemsByRequestId(requestId);
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    String errorText = "Пользователь не найден: " + userId;
                    log.error(errorText);
                    return new EntityNotFoundException(errorText);
                });
    }

    static void validatePageable(Integer from, Integer size) {
        if (from == null || from < 0) {
            throw new ValidationException("Неверный индекс страницы", HttpStatus.BAD_REQUEST);
        }

        if (size == null || size < 0) {
            throw new ValidationException("Неверное количество элементов на странице", HttpStatus.BAD_REQUEST);
        }
    }

}
