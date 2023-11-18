package ru.practicum.shareIt.server.item.model;

import lombok.*;
import ru.practicum.shareIt.server.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "ITEMS")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Column(name = "IS_AVAILABLE")
    private Boolean isAvailable;
    @ManyToOne(fetch = FetchType.LAZY) //use only id
    @JoinColumn(name = "OWNER_ID")
    private User owner;
    private Long requestId;
}
