package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
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

    private User userBooker;
    private User userOwner;
    private Item item;

    @BeforeEach
    void setUp() {
        userBooker = new User();
        userBooker.setEmail("user_booker@gmail.com");
        userBooker.setName("Test User Booker");
        userBooker = userRepository.save(userBooker);

        userOwner = new User();
        userOwner.setEmail("user_owner@gmail.com");
        userOwner.setName("User Owner");
        userOwner = userRepository.save(userOwner);

        item = new Item();
        item.setOwner(userOwner);
        item.setName("Test Item");
        item.setDescription("Item Description");
        item.setAvailable(true);
        item = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(userBooker);
        booking.setStartDate(LocalDateTime.now().plusDays(1));
        booking.setEndDate(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
    }

    @Test
    void testFindAllByBookerId() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingRepository.findAllByBookerId(userBooker.getId(), pageable);
        assertThat(bookings).hasSize(1);

        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
    }

    @Test
    void testFindByBookerIdCurrent() {
        List<Booking> bookings = bookingRepository
                .findByBookerIdCurrent(userBooker.getId(), LocalDateTime.now().plusHours(36));

        assertThat(bookings).isNotEmpty();

        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
    }

    @Test
    void testFindByBookerIdAndEndDateIsBefore() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingRepository
                .findByBookerIdAndEndDateIsBefore(userBooker.getId(), LocalDateTime.now().plusDays(3), pageable);

        assertThat(bookings).isNotEmpty();

        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
    }

    @Test
    void testFindByBookerIdAndStartDateIsAfter() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingRepository
                .findByBookerIdAndStartDateIsAfter(userBooker.getId(), LocalDateTime.now(), pageable);

        assertThat(bookings).isNotEmpty();

        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
    }

    @Test
    void testFindByBookerIdAndStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingRepository
                .findByBookerIdAndStatus(userBooker.getId(), BookingStatus.WAITING, pageable);

        assertThat(bookings).isNotEmpty();

        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
        assertThat(foundBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void testFindAllByItemOwner() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingRepository.findAllByItemOwner(userOwner.getId(), pageable);
        assertThat(bookings).isNotEmpty();

        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
    }

    @Test
    void testFindCurrentBookingsByOwner() {
        List<Booking> bookings = bookingRepository
                .findCurrentBookingsByOwner(userOwner.getId(), LocalDateTime.now().plusHours(36));

        assertThat(bookings).isNotEmpty();

        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
    }

    @Test
    void testFindByItemOwnerIdAndEndDateIsBefore() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingRepository
                .findByItemOwnerIdAndEndDateIsBefore(userOwner.getId(), LocalDateTime.now().plusDays(3), pageable);

        assertThat(bookings).isNotEmpty();

        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
    }

    @Test
    void testFindByItemOwnerIdAndStartDateIsAfter() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStartDateIsAfter(userOwner.getId(), LocalDateTime.now(), pageable);
        assertThat(bookings).isNotEmpty();

        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
    }

    @Test
    void testFindByItemOwnerIdAndStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStatus(userOwner.getId(), BookingStatus.WAITING, pageable);
        assertThat(bookings).isNotEmpty();

        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
        assertThat(foundBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void testFindAllByItemId() {
        Sort sort = Sort.by(Sort.Direction.DESC, "startDate");
        List<Booking> bookings = bookingRepository.findAllByItemId(item.getId(), sort);
        assertThat(bookings).isNotEmpty();

        Booking foundBooking = bookings.get(0);
        assertThat(foundBooking.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundBooking.getBooker().getName()).isEqualTo("Test User Booker");
    }
}
