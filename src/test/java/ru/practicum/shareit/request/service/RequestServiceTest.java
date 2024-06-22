package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private RequestServiceImpl requestService;

    private User user;
    private RequestDto requestDto;
    private Request request;
    private List<ItemDto> items;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User", "user@example.com");
        requestDto = RequestDto.builder()
                .id(1L)
                .description("Request Description")
                .build();
        request = RequestMapper.toEntity(requestDto, user);
        items = List.of(ItemDto.builder().build());
    }

    @Test
    void add_shouldAddRequest_whenRequestIsValid() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.save(any(Request.class))).thenReturn(request);

        RequestDto returnedRequest = requestService.add(requestDto, 1L);

        assertNotNull(returnedRequest);
        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void getAllByOwnerId_shouldReturnRequests_whenRequestsExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestUserId(anyLong(), any(Sort.class))).thenReturn(Collections.singletonList(request));
        when(itemService.getItemsByRequestId(anyLong())).thenReturn(items);

        List<RequestForOwnerDto> requests = requestService.getAllByOwnerId(1L);

        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(items, requests.get(0).getItems());
        verify(requestRepository).findAllByRequestUserId(anyLong(), any(Sort.class));
    }

    @Test
    void getAllByUserIdAndPageable_shouldReturnRequests_whenRequestsExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestUserIdNot(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(Collections.singletonList(request)));
        when(itemService.getItemsByRequestId(anyLong())).thenReturn(items);

        List<RequestWithItemsDto> requests = requestService.getAllByUserIdAndPageable(1L, 0, 10);

        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(items, requests.get(0).getItems());
        verify(requestRepository).findAllByRequestUserIdNot(anyLong(), any(Pageable.class));
    }

    @Test
    void getRequestById_shouldReturnRequest_whenRequestExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemService.getItemsByRequestId(anyLong())).thenReturn(items);

        RequestWithItemsDto returnedRequest = requestService.getRequestById(1L, 1L);

        assertNotNull(returnedRequest);
        assertEquals(items, returnedRequest.getItems());
        verify(requestRepository).findById(anyLong());
    }

    @Test
    void getRequestById_shouldThrowEntityNotFoundException_whenRequestNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> requestService.getRequestById(1L, 1L));

        assertEquals("Запрос на вещь не найден: 1", exception.getMessage());
        verify(requestRepository).findById(anyLong());
    }

    @Test
    void validatePageable_shouldThrowValidationException_whenPageableIsInvalid() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> RequestServiceImpl.validatePageable(-1, 10));

        assertEquals("Неверный индекс страницы", exception.getMessage());

        exception = assertThrows(ValidationException.class,
                () -> RequestServiceImpl.validatePageable(0, -1));

        assertEquals("Неверное количество элементов на странице", exception.getMessage());
    }

}
