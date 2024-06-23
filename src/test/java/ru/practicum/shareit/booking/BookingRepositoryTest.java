package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void testFindAllByBookerId() {
        //given
        Pageable pageable = PageRequest.of(0, 10);

        User userBooker = createUser("user_booker@gmail.com", "Test User Booker");
        User userOwner = createUser("user_owner@gmail.com", "User Owner");
        Item item = createItem(userOwner, "Test Item", "Item Description");
        createBooking(userBooker, item, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

        //that
        List<Booking> bookings = bookingRepository.findAllByBookerId(userBooker.getId(), pageable);

        //then
        assertThat(bookings).hasSize(1);
        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
    }

    @Test
    void testFindByBookerIdCurrent() {
        //given
        User userBooker = createUser("user_booker@gmail.com", "Test User Booker");
        User userOwner = createUser("user_owner@gmail.com", "User Owner");
        Item item = createItem(userOwner, "Test Item", "Item Description");
        createBooking(userBooker, item, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

        //that
        List<Booking> bookings = bookingRepository
                .findByBookerIdCurrent(userBooker.getId(), LocalDateTime.now().plusHours(36));

        //then
        assertThat(bookings).isNotEmpty();
        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
    }

    @Test
    void testFindByBookerIdAndEndDateIsBefore() {
        //given
        Pageable pageable = PageRequest.of(0, 10);

        User userBooker = createUser("user_booker@gmail.com", "Test User Booker");
        User userOwner = createUser("user_owner@gmail.com", "User Owner");
        Item item = createItem(userOwner, "Test Item", "Item Description");
        createBooking(userBooker, item, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

        //that
        List<Booking> bookings = bookingRepository
                .findByBookerIdAndEndDateIsBefore(userBooker.getId(), LocalDateTime.now().plusDays(3), pageable);

        //then
        assertThat(bookings).isNotEmpty();
        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
    }

    @Test
    void testFindByBookerIdAndStartDateIsAfter() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        User userBooker = createUser("user_booker@gmail.com", "Test User Booker");
        User userOwner = createUser("user_owner@gmail.com", "User Owner");
        Item item = createItem(userOwner, "Test Item", "Item Description");
        createBooking(userBooker, item, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

        //that
        List<Booking> bookings = bookingRepository
                .findByBookerIdAndStartDateIsAfter(userBooker.getId(), LocalDateTime.now(), pageable);

        //then
        assertThat(bookings).isNotEmpty();
        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
    }

    @Test
    void testFindByBookerIdAndStatus() {
        //given
        Pageable pageable = PageRequest.of(0, 10);

        User userBooker = createUser("user_booker@gmail.com", "Test User Booker");
        User userOwner = createUser("user_owner@gmail.com", "User Owner");
        Item item = createItem(userOwner, "Test Item", "Item Description");
        createBooking(userBooker, item, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

        //that
        List<Booking> bookings = bookingRepository
                .findByBookerIdAndStatus(userBooker.getId(), BookingStatus.WAITING, pageable);

        //then
        assertThat(bookings).isNotEmpty();
        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
        assertThat(foundBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void testFindAllByItemOwner() {
        //given
        Pageable pageable = PageRequest.of(0, 10);

        User userBooker = createUser("user_booker@gmail.com", "Test User Booker");
        User userOwner = createUser("user_owner@gmail.com", "User Owner");
        Item item = createItem(userOwner, "Test Item", "Item Description");
        createBooking(userBooker, item, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

        //that
        List<Booking> bookings = bookingRepository.findAllByItemOwner(userOwner.getId(), pageable);

        //then
        assertThat(bookings).isNotEmpty();
        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
    }

    @Test
    void testFindCurrentBookingsByOwner() {
        //given
        User userBooker = createUser("user_booker@gmail.com", "Test User Booker");
        User userOwner = createUser("user_owner@gmail.com", "User Owner");
        Item item = createItem(userOwner, "Test Item", "Item Description");
        createBooking(userBooker, item, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

        //that
        List<Booking> bookings = bookingRepository
                .findCurrentBookingsByOwner(userOwner.getId(), LocalDateTime.now().plusHours(36));

        //then
        assertThat(bookings).isNotEmpty();
        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
    }

    @Test
    void testFindByItemOwnerIdAndEndDateIsBefore() {
        //given
        Pageable pageable = PageRequest.of(0, 10);

        User userBooker = createUser("user_booker@gmail.com", "Test User Booker");
        User userOwner = createUser("user_owner@gmail.com", "User Owner");
        Item item = createItem(userOwner, "Test Item", "Item Description");
        createBooking(userBooker, item, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

        //that
        List<Booking> bookings = bookingRepository
                .findByItemOwnerIdAndEndDateIsBefore(userOwner.getId(), LocalDateTime.now().plusDays(3), pageable);

        //then
        assertThat(bookings).isNotEmpty();
        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
    }

    @Test
    void testFindByItemOwnerIdAndStartDateIsAfter() {
        //given
        Pageable pageable = PageRequest.of(0, 10);

        User userBooker = createUser("user_booker@gmail.com", "Test User Booker");
        User userOwner = createUser("user_owner@gmail.com", "User Owner");
        Item item = createItem(userOwner, "Test Item", "Item Description");
        createBooking(userBooker, item, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

        //that
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStartDateIsAfter(userOwner.getId(), LocalDateTime.now(), pageable);

        //then
        assertThat(bookings).isNotEmpty();
        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
    }

    @Test
    void testFindByItemOwnerIdAndStatus() {
        //given
        Pageable pageable = PageRequest.of(0, 10);

        User userBooker = createUser("user_booker@gmail.com", "Test User Booker");
        User userOwner = createUser("user_owner@gmail.com", "User Owner");
        Item item = createItem(userOwner, "Test Item", "Item Description");
        createBooking(userBooker, item, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

        //that
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStatus(userOwner.getId(), BookingStatus.WAITING, pageable);

        //then
        assertThat(bookings).isNotEmpty();
        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
        assertThat(foundBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void testFindAllByItemId() {
        //given
        Sort sort = Sort.by(Sort.Direction.DESC, "startDate");

        User userBooker = createUser("user_booker@gmail.com", "Test User Booker");
        User userOwner = createUser("user_owner@gmail.com", "User Owner");
        Item item = createItem(userOwner, "Test Item", "Item Description");
        createBooking(userBooker, item, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

        //that
        List<Booking> bookings = bookingRepository.findAllByItemId(item.getId(), sort);

        //then
        assertThat(bookings).isNotEmpty();
        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
    }

    private User createUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        return userRepository.save(user);
    }

    private Item createItem(User owner, String name, String description) {
        Item item = new Item();
        item.setOwner(owner);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        return itemRepository.save(item);
    }

    private Booking createBooking(User booker, Item item, LocalDateTime startDate, LocalDateTime endDate, BookingStatus status) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

}
