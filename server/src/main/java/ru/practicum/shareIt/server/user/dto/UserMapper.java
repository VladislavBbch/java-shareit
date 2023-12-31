package ru.practicum.shareIt.server.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareIt.server.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public User toUser(UserDto userDto) {
        return toUser(userDto, userDto.getId());
    }

    public User toUser(UserDto userDto, Long id) {
        return User.builder()
                .id(id)
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public List<UserDto> toUserDto(List<User> users) {
        return users.stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());
    }
}
