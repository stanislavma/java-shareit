package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotFoundException;

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
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingServiceMock;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingDto = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .startDate("2024-06-20T09:00:00")
                .endDate("2024-06-21T09:00:00")
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void add_shouldReturnCreatedBooking_whenBookingIsValid() throws Exception {
        when(bookingServiceMock.add(any(BookingDto.class), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.itemId").value(bookingDto.getItemId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStartDate()))
                .andExpect(jsonPath("$.end").value(bookingDto.getEndDate()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void add_shouldReturnBadRequest_whenItemIdIsNull() throws Exception {
        BookingDto invalidBookingDto = BookingDto.builder()
                .itemId(null)
                .startDate("2024-06-20T12:00:00")
                .endDate("2024-06-21T12:00:00")
                .build();

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(invalidBookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("{itemId=Бронируемая вещь является обязательным полем}"));
    }

    @Test
    void add_shouldReturnBadRequest_whenStartDateIsEmpty() throws Exception {
        BookingDto invalidBookingDto = BookingDto.builder()
                .itemId(1L)
                .startDate("")
                .endDate("2024-06-21T12:00:00")
                .build();

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(invalidBookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("{startDate=Дата начала бронирования является обязательным полем}"));
    }

    @Test
    void add_shouldReturnBadRequest_whenEndDateIsEmpty() throws Exception {
        BookingDto invalidBookingDto = BookingDto.builder()
                .itemId(1L)
                .startDate("2024-06-20T12:00:00")
                .endDate("")
                .build();

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(invalidBookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("{endDate=Дата завершения бронирования является обязательным полем}"));
    }

    @Test
    void updateStatus_shouldReturnUpdatedBooking_whenBookingIsApproved() throws Exception {
        bookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingServiceMock.updateStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void getById_shouldReturnBooking_whenBookingExists() throws Exception {
        when(bookingServiceMock.getById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.itemId").value(bookingDto.getItemId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStartDate()))
                .andExpect(jsonPath("$.end").value(bookingDto.getEndDate()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void getById_shouldReturnError_whenBookingNotExist() throws Exception {
        long nonExistentBookingId = 999L;
        String expectedErrorMessage = "Бронирование не найдено: " + nonExistentBookingId;

        when(bookingServiceMock.getById(anyLong(), anyLong())).thenThrow(new EntityNotFoundException(expectedErrorMessage));

        mockMvc.perform(get("/bookings/{bookingId}", nonExistentBookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));
    }

    @Test
    void getAll_shouldReturnBookings_whenBookingsExist() throws Exception {
        when(bookingServiceMock.getAllByBookerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].itemId").value(bookingDto.getItemId()))
                .andExpect(jsonPath("$[0].start").value(bookingDto.getStartDate()))
                .andExpect(jsonPath("$[0].end").value(bookingDto.getEndDate()))
                .andExpect(jsonPath("$[0].status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void getAllByOwnerId_shouldReturnBookings_whenBookingsExist() throws Exception {
        when(bookingServiceMock.getAllByOwnerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].itemId").value(bookingDto.getItemId()))
                .andExpect(jsonPath("$[0].start").value(bookingDto.getStartDate()))
                .andExpect(jsonPath("$[0].end").value(bookingDto.getEndDate()))
                .andExpect(jsonPath("$[0].status").value(bookingDto.getStatus().toString()));
    }

}