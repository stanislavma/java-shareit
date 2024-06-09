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
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
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
        User user = findUserById(userId);
        Item item = findItemById(bookingDto.getItemId());
        bookingDto.setBookerId(userId);

        validateItemAvailability(item);
        validateStartDateBeforeEndDate(bookingDto);
        validateStartDateNotInPast(bookingDto);

        return BookingMapper.toDto(bookingRepository.save(BookingMapper.toEntity(bookingDto, item, user)));
    }

    @Override
    @Transactional()
    public BookingDto updateStatus(Long bookingId, Long userId, boolean approved) {
        BookingDto bookingDto = getById(bookingId);

        findUserById(userId);
        Item item = findItemById(bookingDto.getItemId());

        validateItemAvailability(item);
        validateStartDateBeforeEndDate(bookingDto);

        if (approved) {
            bookingDto.setStatus(BookingStatus.APPROVED);
        } else {
            bookingDto.setStatus(BookingStatus.REJECTED);
        }

        User booker = findUserById(bookingDto.getBookerId());
        return BookingMapper.toDto(bookingRepository.save(BookingMapper.toEntity(bookingDto, item, booker)));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    String errorText = "Бронирование не найдено: " + bookingId;
                    log.error(errorText);
                    return new EntityNotFoundException(errorText);
                });

        return BookingMapper.toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAll(long userId) {
        findUserById(userId);

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return BookingMapper.toDto(bookingRepository.findAll(sort));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllByUserId(long userId) {
        findUserById(userId);

        return BookingMapper.toDto(bookingRepository.findAllByBookerId(userId));
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

}
