package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto add(BookingDto bookingDto, Long userId) {
        User user = findUserById(userId);
        Item item = findItemById(bookingDto.getItemId());
        bookingDto.setBookerId(userId);

        validateItemAvailability(item);
        validateStartDateBeforeEndDate(bookingDto);
        validateStartDateNotInPast(bookingDto);

        return BookingMapper.toDto(bookingRepository.save(BookingMapper.toEntity(bookingDto, item, user)));
    }

    @Override
    public BookingDto update(BookingDto bookingDto, Long userId) {
        return null;
    }

    @Override
    @Transactional()
    public BookingDto updateStatus(Long bookingId, Long userId, boolean approved) {
        findUserById(userId);

        Booking booking = getBookingById(bookingId);
        BookingDto bookingDto = BookingMapper.toDto(booking);

        validateOwnerAccess(userId, booking.getItem().getOwner().getId());
        validateItemAvailability(booking.getItem());
        validateStartDateBeforeEndDate(bookingDto);

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            System.out.println("check");
        }


        if (approved) {
            bookingDto.setStatus(BookingStatus.APPROVED);
        } else {
            bookingDto.setStatus(BookingStatus.REJECTED);
        }

        User booker = findUserById(bookingDto.getBookerId());
        return BookingMapper.toDto(bookingRepository.save(BookingMapper.toEntity(bookingDto, booking.getItem(), booker)));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getById(Long userId, Long bookingId) {
        findUserById(userId);

        Booking booking = getBookingById(bookingId);

        validateUserAccess(userId, booking.getItem().getOwner().getId(), booking.getBooker().getId());

        return BookingMapper.toDto(booking);
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    String errorText = "Бронирование не найдено: " + bookingId;
                    log.error(errorText);
                    return new EntityNotFoundException(errorText);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAll(long userId, BookingState state) {
        findUserById(userId);

        List<Booking> bookings = getBookingsByState(state);

        return BookingMapper.toDto(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllByOwnerId(long userId, BookingState state) {
        findUserById(userId);

        List<Booking> bookings = getBookingsByStateAndUser(state, userId);

        return BookingMapper.toDto(bookings);
    }

    /**
     * Получает бронирования по состоянию
     */
    private List<Booking> getBookingsByState(BookingState state) {
        List<Booking> bookings = new ArrayList<>();

        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAll(sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookings(LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findByEndDateIsBefore(LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findByStartDateIsAfter(LocalDateTime.now(), sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByStatus(BookingStatus.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findByStatus(BookingStatus.REJECTED, sort);
                break;
            default:
                bookingRepository.findAll(sort);
        }

        return bookings;
    }

    /**
     * Получает бронирования владельца по состоянию
     */
    private List<Booking> getBookingsByStateAndUser(BookingState state, long userId) {
        List<Booking> bookings = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwner(userId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByOwner(userId, LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndDateIsBefore(userId, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerIdAndStartDateIsAfter(userId, LocalDateTime.now(), sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, sort);
                break;
            default:
                bookingRepository.findAllByItemOwner(userId, sort);
        }

        return bookings;
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    String errorText = "Пользователь не найден: " + userId;
                    log.error(errorText);
                    return new EntityNotFoundException(errorText);
                });
    }

    private Item findItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    String errorText = "Вещь не найдена: " + itemId;
                    log.error(errorText);
                    return new EntityNotFoundException(errorText);
                });
    }

    private static void validateItemAvailability(Item item) {
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования", HttpStatus.BAD_REQUEST);
        }
    }

    private static void validateStartDateBeforeEndDate(BookingDto bookingDto) {
        Booking booking = BookingMapper.toEntity(bookingDto, null, null);

        if (booking.getStartDate().isAfter(booking.getEndDate()) ||
                booking.getStartDate().isEqual(booking.getEndDate())) {
            throw new ValidationException("Дата начала бронирования должна быть меньше даты завершения бронирования",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private static void validateStartDateNotInPast(BookingDto bookingDto) {
        Booking booking = BookingMapper.toEntity(bookingDto, null, null);

        if (booking.getStartDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала бронирования не должна быть в прошлом",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private static void validateOwnerAccess(Long userId, Long ownerId) {
        boolean isOwner = ownerId.equals(userId);

        if (!isOwner) {
            throw new ValidationException("Не является владельцем вещи", HttpStatus.NOT_FOUND);
        }
    }

    private static void validateUserAccess(Long userId, Long ownerId, Long bookerId) {
        boolean isOwner = ownerId.equals(userId);
        boolean isBooker = bookerId.equals(userId);

        if (!isOwner && !isBooker) {
            throw new ValidationException("Нет доступа", HttpStatus.NOT_FOUND);
        }

    }

}
