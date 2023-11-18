package ru.practicum.shareit.gateway.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.gateway.Create;
import ru.practicum.shareit.gateway.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
public class UserDto {
    private final Long id;
    @Size(max = 255, groups = {Create.class, Update.class})
    @NotBlank(groups = {Create.class})
    @Email(groups = {Create.class, Update.class})
    private String email;
    @Size(max = 255, groups = {Create.class, Update.class})
    @NotBlank(groups = {Create.class})
    private String name;
}
