package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@Transactional
@Rollback(value = true)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    @MockBean
    private BookingService bookingService;

    @InjectMocks
    private final ItemService service;

    private final UserService userService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userService.getAll().forEach(user -> userService.delete(user.getId()));

        userDto = userService.add(makeUserDto("user_1", "user_1_" + System.currentTimeMillis() + "@gmail.com"));
    }

    @Test
    void add_shouldAdd_whenItemIsValid() {
        ItemDto itemDto = makeItemDto("item_1", "description_1", true);
        ItemDto returnedItemDto = service.add(itemDto, userDto.getId());

        assertThat(returnedItemDto.getId(), notNullValue());
        assertThat(returnedItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(returnedItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(returnedItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void update_shouldUpdate_whenItemIsValid() {
        ItemDto itemDto = makeItemDto("item_2", "description_2", true);
        ItemDto addedItem = service.add(itemDto, userDto.getId());

        ItemDto updateItemDto = ItemDto.builder()
                .id(addedItem.getId())
                .name("updated_item_2")
                .description("updated_description_2")
                .available(false)
                .build();

        ItemDto updatedItem = service.update(updateItemDto, userDto.getId());

        assertThat(updatedItem.getId(), equalTo(addedItem.getId()));
        assertThat(updatedItem.getName(), equalTo(updateItemDto.getName()));
        assertThat(updatedItem.getDescription(), equalTo(updateItemDto.getDescription()));
        assertThat(updatedItem.getAvailable(), equalTo(updateItemDto.getAvailable()));
    }

    @Test
    void getAll_shouldReturnItems_whenItemsExist() {
        ItemDto itemDto1 = makeItemDto("item_4", "description_4", true);
        ItemDto itemDto2 = makeItemDto("item_5", "description_5", true);

        service.add(itemDto1, userDto.getId());
        service.add(itemDto2, userDto.getId());

        List<ItemForOwnerDto> items = service.getItemsByOwnerId(userDto.getId(), 0, 10);

        assertThat(items, hasItem(allOf(
                hasProperty("name", equalTo(itemDto1.getName())),
                hasProperty("description", equalTo(itemDto1.getDescription())),
                hasProperty("available", equalTo(itemDto1.getAvailable()))
        )));
        assertThat(items, hasItem(allOf(
                hasProperty("name", equalTo(itemDto2.getName())),
                hasProperty("description", equalTo(itemDto2.getDescription())),
                hasProperty("available", equalTo(itemDto2.getAvailable()))
        )));
    }

    @Test
    void getById_shouldReturnItem_whenItemExists() {
        ItemDto itemDto = makeItemDto("item_6", "description_6", true);
        ItemDto savedItem = service.add(itemDto, userDto.getId());

        ItemForOwnerDto foundItem = service.getById(userDto.getId(), savedItem.getId());

        assertThat(foundItem.getId(), equalTo(savedItem.getId()));
        assertThat(foundItem.getName(), equalTo(savedItem.getName()));
        assertThat(foundItem.getDescription(), equalTo(savedItem.getDescription()));
        assertThat(foundItem.getAvailable(), equalTo(savedItem.getAvailable()));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void getById_shouldError_whenItemExist() {

        long nonExistentItemId = 999L;

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> service.getById(userDto.getId(), nonExistentItemId));

        assertThat(exception.getMessage(), containsString("Вещь не найдена: " + nonExistentItemId));
    }

    @Test
    void addComment_shouldReturnComment_whenValid() {
        ItemDto itemDto = makeItemDto("item_7", "description_7", true);
        ItemDto savedItem = service.add(itemDto, userDto.getId());

        BookingDto bookingDto = BookingDto.builder()
                .itemId(savedItem.getId())
                .startDate(LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .endDate(LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(BookingStatus.APPROVED)
                .build();

        UserDto userBookerDto = userService.add(makeUserDto("user_2", "user_2@gmail.com"));

        when(bookingService.getAllByBookerId(userBookerDto.getId(), BookingState.PAST.name(), 0, 100))
                .thenReturn(List.of(bookingDto));

        CommentDto commentDto = CommentDto.builder()
                .text("Отличная вещь!")
                .build();

        CommentDto returnedComment = service.addComment(commentDto, userBookerDto.getId(), savedItem.getId());

        assertThat(returnedComment.getId(), notNullValue());
        assertThat(returnedComment.getText(), equalTo(commentDto.getText()));
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        return ItemDto.builder()
                .name(name)
                .description(description)
                .available(available)
                .build();
    }

    private UserDto makeUserDto(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }

}
