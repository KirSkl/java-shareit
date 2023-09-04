package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplIntegrationTest {
     final EntityManager em;
     final UserService service;

    @Test
    void saveUser() {
        User user = makeUser("Пётр","some@email.com");
        service.createUser(user);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User userSaved = query
                .setParameter("email", user.getEmail())
                .getSingleResult();

        assertThat(userSaved.getId(), notNullValue());
        assertThat(userSaved.getName(), equalTo(user.getName()));
        assertThat(userSaved.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void findUserByIdTest() {
        UserDto userSavedDto = service.createUser(makeUser("Пётр","some@email.com"));
        UserDto userDto = service.getUser(userSavedDto.getId());
        assertThat(userDto.getId(), equalTo(userSavedDto.getId()));
        assertThat(userDto.getName(), equalTo(userDto.getName()));
        assertThat(userDto.getEmail(), equalTo(userSavedDto.getEmail()));
    }

     User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}

