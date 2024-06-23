package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@Rollback(value = true)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {

    private final BookingService bookingService;

    @Autowired
    private final UserService userService;

    @Autowired
    private ItemService itemService;

    private final BookingRepository bookingRepository;

    private UserDto userDto;
    private UserDto userBookerDto;

    @BeforeEach
    void setUp() {
        userService.getAll().forEach(user -> userService.delete(user.getId()));
        userDto = userService.add(makeUserDto("user_1", "user_1_" + System.currentTimeMillis() + "@gmail.com"));
        userBookerDto = userService.add(
                makeUserDto("user_booker", "user_booker_" + System.currentTimeMillis() + "@gmail.com"));
    }

    @Test
    void add_shouldAdd_whenBookingIsValid() {
        //given
        ItemDto itemDto = makeItemDto("item_1", "description_1", true, userDto.getId());
        ItemDto addedItem = itemService.add(itemDto, userDto.getId());
        BookingDto bookingDto = getDefaultBooking(addedItem);

        //that
        BookingDto returnedBookingDto = bookingService.add(bookingDto, userBookerDto.getId());

        //then
        assertThat(returnedBookingDto.getId(), notNullValue());
        assertThat(returnedBookingDto.getItemId(), equalTo(addedItem.getId()));
        assertThat(returnedBookingDto.getStartDate(), equalTo(bookingDto.getStartDate()));
        assertThat(returnedBookingDto.getEndDate(), equalTo(bookingDto.getEndDate()));

        Booking foundBooking = bookingRepository.findById(returnedBookingDto.getId()).orElseThrow();
        assertThat(foundBooking.getItem().getId(), equalTo(addedItem.getId()));
        assertThat(foundBooking.getBooker().getId(), equalTo(userBookerDto.getId()));
        assertThat(foundBooking.getStartDate(),
                equalTo(LocalDateTime.parse(bookingDto.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
        assertThat(foundBooking.getEndDate(),
                equalTo(LocalDateTime.parse(bookingDto.getEndDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @Test
    void update_shouldUpdate_whenBookingIsValid() {
        //given
        ItemDto itemDto = makeItemDto("item_2", "description_2", true, userDto.getId());
        ItemDto savedItem = itemService.add(itemDto, userDto.getId());

        BookingDto bookingDto = getDefaultBooking(savedItem);
        BookingDto addedBooking = bookingService.add(bookingDto, userBookerDto.getId());
        BookingDto updateBookingDto = BookingDto.builder()
                .id(addedBooking.getId())
                .itemId(savedItem.getId())
                .startDate(LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .endDate(LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(BookingStatus.APPROVED)
                .build();

        //that
        BookingDto updatedBooking = bookingService.updateStatus(updateBookingDto.getId(), userDto.getId(),
                true);

        //then
        assertThat(updatedBooking.getId(), equalTo(addedBooking.getId()));
        assertThat(updatedBooking.getStatus(), equalTo(updateBookingDto.getStatus()));

        Booking foundBooking = bookingRepository.findById(updatedBooking.getId()).orElseThrow();
        assertThat(foundBooking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getAll_shouldReturnBookings_whenBookingsExist() {
        //given
        ItemDto itemDto1 = makeItemDto("item_4", "description_4", true, userDto.getId());
        ItemDto itemDto2 = makeItemDto("item_5", "description_5", true, userDto.getId());

        ItemDto addedItemDto1 = itemService.add(itemDto1, userDto.getId());
        ItemDto addedItemDto2 = itemService.add(itemDto2, userDto.getId());

        BookingDto bookingDto1 = getDefaultBooking(addedItemDto1);

        BookingDto bookingDto2 = getDefaultBooking(addedItemDto2);

        bookingService.add(bookingDto1, userBookerDto.getId());
        bookingService.add(bookingDto2, userBookerDto.getId());

        //that
        List<BookingDto> bookings = bookingService.getAllByBookerId(
                userBookerDto.getId(), BookingState.ALL.name(), 0, 10);

        //then
        assertThat(bookings, hasItem(allOf(
                hasProperty("itemId", equalTo(bookingDto1.getItemId())),
                hasProperty("startDate", equalTo(bookingDto1.getStartDate())),
                hasProperty("endDate", equalTo(bookingDto1.getEndDate()))
        )));
        assertThat(bookings, hasItem(allOf(
                hasProperty("itemId", equalTo(bookingDto2.getItemId())),
                hasProperty("startDate", equalTo(bookingDto2.getStartDate())),
                hasProperty("endDate", equalTo(bookingDto2.getEndDate()))
        )));

        List<Booking> foundBookings = bookingRepository.findAllByBookerId(userBookerDto.getId(), PageRequest.of(0, 10));
        assertThat(foundBookings, hasItem(hasProperty("item",
                hasProperty("id", equalTo(addedItemDto1.getId())))));
        assertThat(foundBookings, hasItem(hasProperty("item",
                hasProperty("id", equalTo(addedItemDto2.getId())))));

    }

    @Test
    void getById_shouldReturnBooking_whenBookingExists() {
        //given
        ItemDto itemDto = makeItemDto("item_6", "description_6", true, userDto.getId());
        ItemDto savedItem = itemService.add(itemDto, userDto.getId());

        BookingDto bookingDto = getDefaultBooking(savedItem);
        BookingDto addedBooking = bookingService.add(bookingDto, userBookerDto.getId());

        //that
        BookingDto foundBooking = bookingService.getById(userDto.getId(), addedBooking.getId());

        //then
        assertThat(foundBooking.getId(), equalTo(addedBooking.getId()));
        assertThat(foundBooking.getStartDate(), equalTo(addedBooking.getStartDate()));
        assertThat(foundBooking.getEndDate(), equalTo(addedBooking.getEndDate()));

        Booking foundBookingInRepository = bookingRepository.findById(addedBooking.getId()).orElseThrow();
        assertThat(foundBookingInRepository.getId(), equalTo(addedBooking.getId()));
        assertThat(foundBookingInRepository.getStartDate(),
                equalTo(LocalDateTime.parse(addedBooking.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
        assertThat(foundBookingInRepository.getEndDate(),
                equalTo(LocalDateTime.parse(addedBooking.getEndDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void getById_shouldError_whenBookingNotExist() {
        //given
        long nonExistentBookingId = 999L;

        //that
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getById(userDto.getId(), nonExistentBookingId));

        //then
        assertThat(exception.getMessage(), containsString("Бронирование не найдено: " + nonExistentBookingId));
    }

    private BookingDto getDefaultBooking(ItemDto savedItem) {
        return makeBookingDto(savedItem.getId(), userDto.getId(),
                LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    private ItemDto makeItemDto(String name, String description, Boolean available, Long ownerId) {
        return ItemDto.builder()
                .name(name)
                .description(description)
                .available(available)
                .ownerId(ownerId)
                .build();
    }

    private BookingDto makeBookingDto(Long itemId, Long bookerId, String startDate, String endDate) {
        return BookingDto.builder()
                .itemId(itemId)
                .bookerId(bookerId)
                .startDate(startDate)
                .endDate(endDate)
                .status(BookingStatus.WAITING)
                .build();
    }

    private UserDto makeUserDto(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }

}
