package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplementTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceImpl userService;
    UserDto userDto;
    UserDto userDtoUpdate;
    User user;
    User userUpdate;

    @BeforeEach
    void loadInitial() {
        userDto = new UserDto(1L, "John Doe", "John_Doe@mail.com");
        user = new User(userDto.getId(), userDto.getName(), userDto.getEmail());
        userUpdate = new User(userDto.getId(), "John Update", "update@mail.com");
        userDtoUpdate = new UserDto(userDto.getId(), "John Update", "update@mail.com");

    }

    @Test
    void testGetAllOk() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        var result = userService.getAll();

        assertEquals(List.of(userDto), result);
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

    @Test
    void testGetAllIfEmptyOk() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        var result = userService.getAll();

        assertEquals(Collections.emptyList(), result);
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

    @Test
    void testCreateUserOk() {
        when(userRepository.save(user)).thenReturn(user);

        var result = userService.createUser(user);

        assertEquals(userDto, result);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    void testUpdateUserOk() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(user)).thenReturn(user);

        var result = userService.updateUser(userUpdate.getId(), userUpdate);

        assertEquals(userDtoUpdate, result);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verify(userRepository, Mockito.times(1)).findById(userUpdate.getId());
    }

    @Test
    void testUpdateUserThrownNotFound() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(userUpdate.getId(), userUpdate));
        Mockito.verify(userRepository, Mockito.never()).save(userUpdate);
    }

    @Test
    void testDeleteUserOk() {
        when(userRepository.deleteByIdAndReturnCount(anyLong())).thenReturn(1);

        userService.deleteUser(user.getId());

        Mockito.verify(userRepository, Mockito.times(1)).deleteByIdAndReturnCount(user.getId());
    }

    @Test
    void testDeleteUserThrownNotFound() {
        when(userRepository.deleteByIdAndReturnCount(anyLong())).thenReturn(0);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(anyLong()));
    }

    @Test
    void testGetUserOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        var result = userService.getUser(user.getId());

        assertEquals(userDto, result);
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
    }

    @Test
    void testGetUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(user.getId()));
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
    }

}
