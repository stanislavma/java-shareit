package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final BookingService bookingService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private final Sort itemsSort = Sort.by(Sort.Direction.ASC, "id");
    private final RequestRepository requestRepository;

    @Override
    @Transactional()
    public ItemDto add(ItemDto itemDto, Long userId) {
        User user = findUserById(userId);
        Request request = findRequestById(itemDto.getRequestId());

        Item item = ItemMapper.toEntity(itemDto, user, request);
        item.setOwner(user);

        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional()
    public ItemDto update(ItemDto itemDto, Long userId) {
        findUserById(userId); // если пользователя нет, то метод сам пробросит ошибку

        Item existingItem = getItemById(itemDto.getId());

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Нет доступа к вещи!");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toDto(itemRepository.save(existingItem));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemForOwnerDto> getItemsByOwnerId(Long userId, Integer from, Integer size) {
        validatePageable(from, size);

        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, itemsSort);
        List<Item> items = itemRepository.findAllByOwnerId(userId, pageable);

        List<ItemForOwnerDto> itemForOwnerDtoList = new ArrayList<>();
        for (Item item : items) {
            ItemForOwnerDto itemForOwnerDto = ItemMapper.toEntityForOwnerDto(item);

            setBookingsToItem(userId, item, itemForOwnerDto);
            setCommentsToItem(item, itemForOwnerDto);

            itemForOwnerDtoList.add(itemForOwnerDto);
        }

        return itemForOwnerDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByRequestId(Long requestId) {
        List<Item> items = itemRepository.findAllByRequestId(requestId, itemsSort);

        return ItemMapper.toDto(items);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemForOwnerDto getById(Long userId, Long itemId) {
        findUserById(userId);
        Item item = getItemById(itemId);

        ItemForOwnerDto itemForOwnerDto = ItemMapper.toEntityForOwnerDto(item);

        setBookingsToItem(userId, item, itemForOwnerDto);
        setCommentsToItem(item, itemForOwnerDto);

        return itemForOwnerDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByText(String text, Integer from, Integer size) {
        validatePageable(from, size);

        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }

        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, itemsSort);
        return ItemMapper.toDto(itemRepository.findByText(text, pageable));
    }

    @Override
    @Transactional()
    public CommentDto addComment(CommentDto commentDto, Long userId, Long itemId) {
        User author = findUserById(userId);
        Item item = getItemById(itemId);

        List<BookingDto> bookingDtoList =
                bookingService.getAllByBookerId(userId, BookingState.PAST.name(), 0, 100);

        boolean hasValidBooking = bookingDtoList.stream()
                .anyMatch(booking -> booking.getItemId()
                        .equals(itemId) && booking.getStatus() == BookingStatus.APPROVED);

        if (!hasValidBooking) {
            throw new ValidationException("Пользователь не арендовал эту вещь", HttpStatus.BAD_REQUEST);
        }

        Comment comment = CommentMapper.toEntity(commentDto, item, author);

        return CommentMapper.toDto(commentRepository.save(comment));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    String errorText = "Вещь не найдена: " + itemId;
                    log.error(errorText);
                    return new EntityNotFoundException(errorText);
                });
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    String errorText = "Пользователь не найден: " + userId;
                    log.error(errorText);
                    return new EntityNotFoundException(errorText);
                });
    }

    private Request findRequestById(Long requestId) {
        Request request = null;

        if (requestId != null && requestId > 0) {
            request = requestRepository.findById(requestId)
                    .orElseThrow(() -> {
                        String errorText = "Запрос на вещь не найден: " + requestId;
                        log.error(errorText);
                        return new EntityNotFoundException(errorText);
                    });
        }

        return request;
    }

    private void setBookingsToItem(Long userId, Item item, ItemForOwnerDto itemForOwnerDto) {
        if (item.getOwner().getId().equals(userId)) {
            List<Booking> bookings = bookingRepository.findAllByItemId(item.getId(), itemsSort);

            Booking lastBooking = getLastBooking(bookings);
            Booking nextBooking = getNextBooking(bookings);

            itemForOwnerDto.setLastBooking(BookingMapper.toShortDto(lastBooking));
            itemForOwnerDto.setNextBooking(BookingMapper.toShortDto(nextBooking));
        }
    }

    private void setCommentsToItem(Item item, ItemForOwnerDto itemForOwnerDto) {
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());

        List<CommentDto> commentDtoList = CommentMapper.toDto(comments);
        itemForOwnerDto.setComments(commentDtoList);
    }

    private static Booking getLastBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(b -> b.getStatus().equals(BookingStatus.APPROVED))
                .filter(b -> b.getStartDate().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getStartDate))
                .orElse(null);
    }

    private static Booking getNextBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(b -> b.getStatus().equals(BookingStatus.APPROVED))
                .filter(b -> b.getStartDate().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStartDate))
                .orElse(null);
    }

    private static void validatePageable(Integer from, Integer size) {
        if (from == null || from < 0) {
            throw new ValidationException("Неверный индекс страницы", HttpStatus.BAD_REQUEST);
        }

        if (size == null || size < 0) {
            throw new ValidationException("Неверное количество элементов на странице", HttpStatus.BAD_REQUEST);
        }
    }

}