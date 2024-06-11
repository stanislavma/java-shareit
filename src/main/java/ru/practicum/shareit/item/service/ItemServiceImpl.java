package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
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

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private final Sort itemsSort = Sort.by(Sort.Direction.ASC, "id");

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        User user = findUserById(userId);

        itemDto.setOwnerId(userId);
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto, user, null)));
    }

    @Override
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

        return ItemMapper.toItemDto(itemRepository.save(existingItem));
    }

    @Override
    public List<ItemForOwnerDto> getItemsByOwnerId(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId, itemsSort);

        List<ItemForOwnerDto> itemForOwnerDtoList = new ArrayList<>();
        for (Item item : items) {
            ItemForOwnerDto itemForOwnerDto = ItemMapper.toItemForOwnerDto(item);
            setBookingsToItem(userId, item, itemForOwnerDto);
            itemForOwnerDtoList.add(itemForOwnerDto);
        }

        return itemForOwnerDtoList;
    }

    @Override
    public ItemForOwnerDto getById(Long userId, Long itemId) {
        findUserById(userId);

        Item item = getItemById(itemId);
        ItemForOwnerDto itemForOwnerDto = ItemMapper.toItemForOwnerDto(item);
        setBookingsToItem(userId, item, itemForOwnerDto);

        return itemForOwnerDto;
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }

        return ItemMapper.toItemDto(itemRepository.findByText(text));
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

//    private static Booking getLastBooking(List<Booking> bookings) {
//        return bookings.stream()
//                .filter(b -> b.getStartDate().isBefore(LocalDateTime.now()))
//                .max(Comparator.comparing(Booking::getStartDate))
//                .orElse(null);
//    }
//
//    private static Booking getNextBooking(List<Booking> bookings) {
//        return bookings.stream()
//                .filter(b -> b.getStartDate().isAfter(LocalDateTime.now()))
//                .min(Comparator.comparing(Booking::getStartDate))
//                .orElse(null);
//    }


    private static Booking getLastBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(b -> b.getStartDate().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getStartDate))
                .orElse(null);
    }

    private static Booking getNextBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(b -> b.getStartDate().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStartDate))
                .orElse(null);
    }

}