package ru.practicum.shareIt.server.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class UserDto {
    private final Long id;
    private String email;
    private String name;
}
