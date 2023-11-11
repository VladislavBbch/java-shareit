package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService должен")
public class UserServiceTest {
    @InjectMocks
    UserService userService;
    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;

    @DisplayName("создавать пользователя")
    @Test
    void createUser_whenUserValid_thenCreateUser() {
        User user = new User();
        UserDto userDto = UserDto.builder().build();
        when(userMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.createUser(userDto);

        assertEquals(result, userDto);
        verify(userRepository, times(1)).save(user);
    }

    @DisplayName("возвращать пользователей")
    @Test
    void getUsers_whenUsersExist_thenReturnUsers() {
        User user = new User();
        List<User> users = List.of(user);
        UserDto userDto = UserDto.builder().build();
        List<UserDto> userDtos = List.of(userDto);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toUserDto(users)).thenReturn(userDtos);

        List<UserDto> result = userService.getUsers();

        assertEquals(result, userDtos);
        verify(userRepository, times(1)).findAll();
    }

    @DisplayName("возвращать пользователя по id")
    @Test
    void getUserById_whenUserExistAndOwner_thenReturnUser() {
        Long userId = 1L;
        User user = new User();
        UserDto userDto = UserDto.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserById(userId);

        assertEquals(result, userDto);
        verify(userRepository, times(1)).findById(userId);
    }

    @DisplayName("обновлять пользователя")
    @Test
    void updateUser_whenUserExist_thenUpdateUser() {
        Long userId = 1L;
        User existingUser = new User(userId, "user@mail.ru", "Пользователь");
        UserDto existingUserDto = UserDto.builder()
                .id(existingUser.getId())
                .email(existingUser.getEmail())
                .name(existingUser.getName())
                .build();
        User user = new User(userId, "updateduser@mail.ru", "ОбновленныйПользователь");
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userMapper.toUserDto(existingUser)).thenReturn(existingUserDto);
        when(userMapper.toUser(existingUserDto)).thenReturn(existingUser);
        when(userMapper.toUser(existingUserDto, userId)).thenReturn(user);
        when(userRepository.save(existingUser)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.updateUser(userId, existingUserDto);

        assertEquals(result, userDto);
        verify(userRepository, times(1)).save(existingUser);
    }

    @DisplayName("удалять пользователя")
    @Test
    void deleteUser_whenUserExistAndOwner_thenDeleteUser() {
        Long userId = 1L;
        User user = new User();
        UserDto userDto = UserDto.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }
}
