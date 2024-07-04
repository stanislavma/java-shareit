package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingDto bookingDto;
    private Booking booking;
    private User user;
    private User userBooker;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User", "user@gmail.com");
        userBooker = new User(2L, "User Booker", "user_booker@gmail.com");

        item = new Item(1L, "Item", "Description", true, user, null);
        bookingDto = BookingDto.builder()
                .id(1L)
                .itemId(item.getId())
                .bookerId(user.getId())
                .startDate(LocalDateTime.now().plusDays(1).toString())
                .endDate(LocalDateTime.now().plusDays(2).toString())
                .build();
        booking = BookingMapper.toEntity(bookingDto, item, user);
    }

    @Test
    void add_shouldAddBooking_whenBookingIsValid() {
        doReturn(Optional.of(user)).when(userRepository).findById(anyLong());
        doReturn(Optional.of(item)).when(itemRepository).findById(anyLong());
        doReturn(booking).when(bookingRepository).save(any(Booking.class));

        BookingDto returnedBooking = bookingService.add(bookingDto, userBooker.getId());

        assertNotNull(returnedBooking);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void add_shouldThrowValidationException_whenItemIsUnavailable() {
        item.setAvailable(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.add(bookingDto, 1L));

        assertEquals("Вещь не доступна для бронирования", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void add_shouldThrowValidationException_whenStartDateIsInPast() {
        bookingDto.setStartDate(LocalDateTime.now().minusDays(1).toString());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.add(bookingDto, 1L));

        assertEquals("Дата начала бронирования не должна быть в прошлом", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateStatus_shouldUpdateBookingStatus_whenBookingIsValid() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.updateStatus(1L, 1L, true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void updateStatus_shouldThrowValidationException_whenBookingIsAlreadyApproved() {
        booking.setStatus(BookingStatus.APPROVED);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.updateStatus(1L, 1L, true));

        assertEquals("Уже подтвержден владельцем", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getById_shouldReturnBooking_whenBookingExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto returnedBooking = bookingService.getById(1L, 1L);

        assertNotNull(returnedBooking);
        assertEquals(bookingDto.getId(), returnedBooking.getId());
        verify(bookingRepository).findById(anyLong());
    }

    @Test
    void getById_shouldThrowEntityNotFoundException_whenBookingNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getById(1L, 1L));

        assertEquals("Бронирование не найдено: 1", exception.getMessage());
        verify(bookingRepository).findById(anyLong());
    }

    @Test
    void getAllByBookerId_shouldReturnBookings_whenStateIsAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerId(anyLong(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByBookerId(1L, "ALL", 0, 10);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository).findAllByBookerId(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllByBookerId_shouldReturnBookings_whenStateIsCurrent() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdCurrent(anyLong(), any(LocalDateTime.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByBookerId(1L, "CURRENT", 0, 10);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository).findByBookerIdCurrent(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void getAllByBookerId_shouldReturnBookings_whenStateIsPast() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndEndDateIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByBookerId(1L, "PAST", 0, 10);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository).findByBookerIdAndEndDateIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByBookerId_shouldReturnBookings_whenStateIsFuture() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStartDateIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByBookerId(1L, "FUTURE", 0, 10);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository).findByBookerIdAndStartDateIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByBookerId_shouldReturnBookings_whenStateIsWaiting() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStatus(anyLong(), eq(BookingStatus.WAITING), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByBookerId(1L, "WAITING", 0, 10);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository).findByBookerIdAndStatus(anyLong(), eq(BookingStatus.WAITING), any(Pageable.class));
    }

    @Test
    void getAllByBookerId_shouldReturnBookings_whenStateIsRejected() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStatus(anyLong(), eq(BookingStatus.REJECTED), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByBookerId(1L, "REJECTED", 0, 10);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository).findByBookerIdAndStatus(anyLong(), eq(BookingStatus.REJECTED), any(Pageable.class));
    }

    @Test
    void getAllByOwnerId_shouldReturnBookings_whenStateIsAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwner(anyLong(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByOwnerId(1L, "ALL", 0, 10);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository).findAllByItemOwner(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllByOwnerId_shouldReturnBookings_whenStateIsCurrent() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findCurrentBookingsByOwner(anyLong(), any(LocalDateTime.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByOwnerId(1L, "CURRENT", 0, 10);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository).findCurrentBookingsByOwner(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void getAllByOwnerId_shouldReturnBookings_whenStateIsPast() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdAndEndDateIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByOwnerId(1L, "PAST", 0, 10);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository).findByItemOwnerIdAndEndDateIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerId_shouldReturnBookings_whenStateIsFuture() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdAndStartDateIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByOwnerId(1L, "FUTURE", 0, 10);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository).findByItemOwnerIdAndStartDateIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerId_shouldReturnBookings_whenStateIsWaiting() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdAndStatus(anyLong(), eq(BookingStatus.WAITING), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByOwnerId(1L, "WAITING", 0, 10);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository).findByItemOwnerIdAndStatus(anyLong(), eq(BookingStatus.WAITING), any(Pageable.class));
    }

    @Test
    void getAllByOwnerId_shouldReturnBookings_whenStateIsRejected() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdAndStatus(anyLong(), eq(BookingStatus.REJECTED), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByOwnerId(1L, "REJECTED", 0, 10);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(bookingRepository).findByItemOwnerIdAndStatus(anyLong(), eq(BookingStatus.REJECTED), any(Pageable.class));
    }

    @Test
    void getAllByBookerId_shouldThrowValidationException_whenPageableIsInvalid() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.getAllByBookerId(1L, "ALL", -1, 10));

        assertEquals("Неверный индекс страницы", exception.getMessage());
    }

    @Test
    void getAllByOwnerId_shouldThrowValidationException_whenPageableIsInvalid() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.getAllByOwnerId(1L, "ALL", -1, 10));

        assertEquals("Неверный индекс страницы", exception.getMessage());
    }

}
