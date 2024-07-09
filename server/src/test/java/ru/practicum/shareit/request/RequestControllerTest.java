package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestForOwnerDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.common.Constants.X_SHARER_USER_ID;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestServiceMock;

    @Autowired
    private ObjectMapper objectMapper;

    private RequestDto requestDto;
    private RequestForOwnerDto requestForOwnerDto;
    private RequestWithItemsDto requestWithItemsDto;

    @BeforeEach
    void setUp() {
        requestDto = RequestDto.builder()
                .id(1L)
                .description("Request Description")
                .build();

        requestForOwnerDto = RequestForOwnerDto.builder()
                .id(1L)
                .description("Request Description")
                .build();

        requestWithItemsDto = RequestWithItemsDto.builder()
                .id(1L)
                .description("Request Description")
                .build();
    }

    @Test
    void add_shouldReturnRequest_whenRequestIsValid() throws Exception {
        when(requestServiceMock.add(any(RequestDto.class), anyLong())).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()));
    }

    @Test
    void add_shouldReturnBadRequest_whenDescriptionIsEmpty() throws Exception {
        RequestDto invalidRequestDto = RequestDto.builder()
                .description("")
                .build();

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(invalidRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("{description=Не должно быть пустым}"));
    }

    @Test
    void getAllByOwnerId_shouldReturnRequests_whenRequestsExist() throws Exception {
        when(requestServiceMock.getAllByOwnerId(anyLong())).thenReturn(List.of(requestForOwnerDto));

        mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(requestForOwnerDto.getId()))
                .andExpect(jsonPath("$[0].description").value(requestForOwnerDto.getDescription()));
    }

    @Test
    void getAllByUserIdAndPageable_shouldReturnRequests_whenRequestsExist() throws Exception {
        when(requestServiceMock.getAllByUserIdAndPageable(anyLong(), anyInt(), anyInt())).thenReturn(List.of(requestWithItemsDto));

        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(requestWithItemsDto.getId()))
                .andExpect(jsonPath("$[0].description").value(requestWithItemsDto.getDescription()));
    }

    @Test
    void getById_shouldReturnRequest_whenRequestExists() throws Exception {
        when(requestServiceMock.getRequestById(anyLong(), anyLong())).thenReturn(requestWithItemsDto);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestWithItemsDto.getId()))
                .andExpect(jsonPath("$.description").value(requestWithItemsDto.getDescription()));
    }

    @Test
    void getById_shouldReturnError_whenRequestNotExist() throws Exception {
        long nonExistentRequestId = 999L;
        String expectedErrorMessage = "Запрос на вещь не найден: " + nonExistentRequestId;

        when(requestServiceMock.getRequestById(anyLong(), anyLong())).thenThrow(new EntityNotFoundException(expectedErrorMessage));

        mockMvc.perform(get("/requests/{requestId}", nonExistentRequestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));
    }

}