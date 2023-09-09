package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectExistException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        checkUserEmail(user);
        return userMapper.toUserDto(userRepository.create(user));
    }

    public List<UserDto> getUsers() {
        return userMapper.toUserDto(userRepository.read());
    }

    public UserDto getUserById(Long userId) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id пользователя: " + userId));
        return userMapper.toUserDto(user);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        getUserById(id);
        User user = userMapper.toUser(userDto, id);
        if (user.getEmail() != null) {
            checkUserEmail(user);
        }
        return userMapper.toUserDto(userRepository.update(user));
    }

    public void deleteUser(Long userId) {
        getUserById(userId);
        userRepository.delete(userId);
    }

    private void checkUserEmail(User user) {
        Optional<User> existingUser = userRepository.getByEmail(user.getEmail());
        if (existingUser.isPresent() && !Objects.equals(existingUser.get().getId(), user.getId())) {
            throw new ObjectExistException("Пользователь с данным email: '" + user.getEmail() + "' уже существует");
        }
    }
}
