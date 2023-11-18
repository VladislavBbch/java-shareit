package ru.practicum.shareIt.server.item.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareIt.server.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "COMMENTS")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @CreationTimestamp
    private LocalDateTime created;
    @ManyToOne(fetch = FetchType.LAZY) //use only id
    @JoinColumn(name = "ITEM_ID")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "AUTHOR_ID")
    private User author;
}
