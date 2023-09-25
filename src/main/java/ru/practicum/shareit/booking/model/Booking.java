package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "BOOKINGS")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "START_TIMESTAMP")
    private LocalDateTime start;
    @Column(name = "END_TIMESTAMP")
    private LocalDateTime end;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    @ManyToOne(fetch = FetchType.LAZY) //use only id
    @JoinColumn(name = "BOOKER_ID")
    private User booker;
    @ManyToOne
    @JoinColumn(name = "ITEM_ID")
    private Item item;
}
