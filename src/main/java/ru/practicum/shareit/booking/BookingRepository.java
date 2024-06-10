package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE :now BETWEEN b.startDate AND b.endDate " +
            "AND b.booker.id = :bookerId")
    List<Booking> findByBookerIdCurrent(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    List<Booking> findByBookerIdAndEndDateIsBefore(Long bookerId, LocalDateTime endDate, Sort sort);

    List<Booking> findByBookerIdAndStartDateIsAfter(Long bookerId, LocalDateTime startDate, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllByItemOwner(@Param("ownerId") Long ownerId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND :now BETWEEN b.startDate AND b.endDate " +
            "ORDER BY b.startDate DESC")
    List<Booking> findCurrentBookingsByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    List<Booking> findByItemOwnerIdAndEndDateIsBefore(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerIdAndStartDateIsAfter(Long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);
}
