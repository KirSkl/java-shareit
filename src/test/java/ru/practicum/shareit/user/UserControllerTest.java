package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.common.Validator;
import ru.practicum.shareit.exceptions.ValidationIdException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    UserService service;
    @MockBean
    Validator validator;

    Long userId;
    User user;
    UserDto userDto;

    @BeforeEach
    void loadInitial() {
        userId = 1L;
        userDto = new UserDto(1L, "John Doe", "John_Doe@mail.com");
        user = new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    @SneakyThrows
    @Test
    void getAllUsersOk() {
        when(service.getAll())
                .thenReturn(List.of(userDto));

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));
    }

    @SneakyThrows
    @Test
    void testGetUserOk() {
        when(service.getUser(userId)).thenReturn(userDto);

        mvc.perform(get("/users/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        InOrder order = inOrder(validator, service);
        order.verify(validator, times(1)).validateId(userId);
        order.verify(service, times(1)).getUser(userId);
    }

    @SneakyThrows
    @Test
    void testGetUserInvalidIdThrown() {
        doThrow(new ValidationIdException("Неверный id")).when(validator).validateId(userId);

        mvc.perform(get("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, never()).getUser(anyLong());
    }

    @SneakyThrows
    @Test
    void testCreateUserOk() {
        when(service.createUser(user)).thenReturn(userDto);

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @SneakyThrows
    @Test
    void testCreateUserNameIsBlankThrown() {
        user.setName("");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, never()).createUser(any());
    }

    @SneakyThrows
    @Test
    void testCreateUserWrongEmailThrown() {
        user.setEmail("wrongEmail");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, never()).createUser(any());
    }

    @SneakyThrows
    @Test
    void testUpdateUserOk() {
        when(service.updateUser(userId, user)).thenReturn(userDto);

        mvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(service, times(1)).updateUser(userId, user);
    }

    @SneakyThrows
    @Test
    void testUpdateUserEmailIsBlankThrown() {
        user.setEmail("");

        mvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, never()).updateUser(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void testDeleteUserOk() {
        mvc.perform(delete("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service, times(1)).deleteUser(userId);
    }
}
