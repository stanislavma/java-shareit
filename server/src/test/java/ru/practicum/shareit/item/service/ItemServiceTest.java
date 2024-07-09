package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private ItemServiceImpl itemService;

    private ItemDto itemDto;
    private Item item;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User", "user@gmail.com");
        itemDto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .ownerId(user.getId())
                .build();
        item = ItemMapper.toEntity(itemDto, user, null);
    }

    @Test
    void add_shouldAddItem_whenItemIsValid() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto returnedItem = itemService.add(itemDto, 1L);

        assertNotNull(returnedItem);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void update_shouldUpdateItem_whenItemIsValid() {
        ItemDto updatedItemDto = ItemDto.builder()
                .id(1L)
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .ownerId(user.getId())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(ItemMapper.toEntity(updatedItemDto, user, null));

        ItemDto result = itemService.update(updatedItemDto, 1L);

        assertNotNull(result);
        assertEquals(updatedItemDto.getId(), result.getId());
        assertEquals(updatedItemDto.getName(), result.getName());
        assertEquals(updatedItemDto.getDescription(), result.getDescription());
        assertEquals(updatedItemDto.getAvailable(), result.getAvailable());

        verify(itemRepository).findById(1L);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void getItemsByOwnerId_shouldReturnItems_whenItemsExist() {
        when(itemRepository.findAllByOwnerId(anyLong(), any(Pageable.class))).thenReturn(List.of(item));

        List<Booking> bookings = List.of(
                new Booking(1L, item, user, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), BookingStatus.APPROVED),
                new Booking(2L, item, user, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.APPROVED)
        );

        when(bookingRepository.findAllByItemId(anyLong(), any(Sort.class))).thenReturn(bookings);

        List<ItemForOwnerDto> items = itemService.getItemsByOwnerId(1L, 0, 10);

        assertNotNull(items);
        assertEquals(1, items.size());
        verify(itemRepository).findAllByOwnerId(anyLong(), any(Pageable.class));
        verify(bookingRepository).findAllByItemId(anyLong(), any(Sort.class));
    }

    @Test
    void getItemsByRequestId_shouldReturnItems_whenItemsExist() {
        when(itemRepository.findAllByRequestId(anyLong(), any(Sort.class))).thenReturn(List.of(item));

        List<ItemDto> items = itemService.getItemsByRequestId(1L);

        assertNotNull(items);
        assertEquals(1, items.size());
        verify(itemRepository).findAllByRequestId(anyLong(), any(Sort.class));
    }

    @Test
    void getById_shouldReturnItem_whenItemExists() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ItemForOwnerDto returnedItem = itemService.getById(1L, 1L);

        assertNotNull(returnedItem);
        assertEquals(itemDto.getId(), returnedItem.getId());
        verify(itemRepository).findById(1L);
    }

    @Test
    void getItemsByText_shouldReturnItems_whenItemsExist() {
        when(itemRepository.findByText(anyString(), any(Pageable.class))).thenReturn(List.of(item));

        List<ItemDto> items = itemService.getItemsByText("Item", 0, 10);

        assertNotNull(items);
        assertEquals(1, items.size());
        verify(itemRepository).findByText(anyString(), any(Pageable.class));
    }

    @Test
    void addComment_shouldAddComment_whenCommentIsValid() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Great item!")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.save(any(Comment.class))).thenReturn(CommentMapper.toEntity(commentDto, item, user));
        when(bookingService.getAllByBookerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(BookingDto.builder().itemId(1L).status(BookingStatus.APPROVED).build()));

        CommentDto returnedComment = itemService.addComment(commentDto, 1L, 1L);

        assertNotNull(returnedComment);
        assertEquals(commentDto.getText(), returnedComment.getText());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_shouldThrowValidationException_whenNoValidBooking() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Great item!")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingService.getAllByBookerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.addComment(commentDto, 1L, 1L));

        assertEquals("Пользователь не арендовал эту вещь", exception.getMessage());
        verify(commentRepository, never()).save(any(Comment.class));
    }
}
