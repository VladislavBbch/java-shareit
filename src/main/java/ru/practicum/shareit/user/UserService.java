package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userRepository.save(user));
    }

    public List<UserDto> getUsers() {
        return userMapper.toUserDto(userRepository.findAll());
    }

    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id пользователя: " + userId));
        return userMapper.toUserDto(user);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User existingUser = userMapper.toUser(getUserById(id));
        User user = userMapper.toUser(userDto, id);

        String email = user.getEmail();
        if (email != null && !email.isBlank()) {
            existingUser.setEmail(email);
        }
        String name = user.getName();
        if (name != null && !name.isBlank()) {
            existingUser.setName(name);
        }
        return userMapper.toUserDto(userRepository.save(existingUser));
    }

    public void deleteUser(Long userId) {
        getUserById(userId);
        userRepository.deleteById(userId);
    }
}
