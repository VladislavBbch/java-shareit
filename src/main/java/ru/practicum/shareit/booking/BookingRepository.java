package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId,
                                                             LocalDateTime startBefore,
                                                             LocalDateTime endAfter,
                                                             Sort sort);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime startAfter, Sort sort);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime endBefore, Sort sort);

    List<Booking> findAllByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(Long ownerId,
                                                                LocalDateTime startBefore,
                                                                LocalDateTime endAfter,
                                                                Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime startAfter, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime endBefore, Sort sort);

    @Query("select case when (count(b) > 0) then true else false end " +
            "from Booking b " +
            "where b.start < ?3 and b.end > ?2 " +
            "and b.item.id = ?1 " +
            "and b.status = 'APPROVED'")
    Boolean isItemBookingNotAvailableForDates(Long itemId, LocalDateTime start, LocalDateTime end);

    Boolean existsAllByBookerIdAndItemIdAndStatusAndEndBefore(
            Long bookerId, Long itemId, BookingStatus status, LocalDateTime now);

    List<Booking> findAllByItemIdAndStatus(Long itemId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemIdInAndStatus(List<Long> itemIds, BookingStatus status, Sort sort);
}
