package ru.practicum.shareIt.server.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareIt.server.user.dto.UserDto;
import ru.practicum.shareIt.server.user.dto.UserMapper;
import ru.practicum.shareIt.server.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserMapper должен")
public class UserMapperTest {
    private final UserMapper userMapper = new UserMapper();

    @DisplayName("мапить сущность в dto")
    @Test
    void shouldMapUserToUserDto() {
        Long userId = 1L;
        User user = new User(userId, "user@mail.ru", "Пользователь");

        List<UserDto> userDtos = userMapper.toUserDto(List.of(user));

        assertEquals(user.getId(), userDtos.get(0).getId());
        assertEquals(user.getEmail(), userDtos.get(0).getEmail());
        assertEquals(user.getName(), userDtos.get(0).getName());
    }

    @DisplayName("мапить dto в сущность")
    @Test
    void shouldMapUserDtoToUser() {
        UserDto userDto = UserDto.builder()
                .email("user@mail.ru")
                .name("Пользователь")
                .build();

        User user = userMapper.toUser(userDto);

        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getName(), user.getName());
    }
}
