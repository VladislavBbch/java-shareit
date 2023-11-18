package ru.practicum.shareIt.server.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareIt.server.item.model.Comment;
import ru.practicum.shareIt.server.item.model.Item;
import ru.practicum.shareIt.server.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {
    public Comment toComment(Long userId, Long itemId, CommentDtoRequest commentDto) {
        return Comment.builder()
                .author(User.builder().id(userId).build())
                .item(Item.builder().id(itemId).build())
                .text(commentDto.getText())
                .build();
    }

    public CommentDtoResponse toCommentDto(Comment comment) {
        return CommentDtoResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public List<CommentDtoResponse> toCommentDto(List<Comment> comments) {
        return comments.stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
    }
}
