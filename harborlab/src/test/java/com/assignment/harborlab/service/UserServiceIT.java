package com.assignment.harborlab.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.assignment.harborlab.exception.UserExistsException;
import com.assignment.harborlab.repository.UserRepository;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceIT {

    public static final String USER = "Markos";
    public static final String EMAIL = "markos@test.com";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateRoom() {
        final var user = userService.createUser("Nikos", "nikos@test.com");

        final var retrievedUser = userRepository.findByEmail(user.getEmail());

        assertThat(user.getName()).isNotNull();
        assertThat(user.getName()).isEqualTo(retrievedUser.getName());
        assertThat(user.getEmail()).isEqualTo(retrievedUser.getEmail());
    }

    @Test
    void testDuplicateUserEmailThrowsException() {
        userService.createUser("Nikos", "nikos@test.com");

        final var ex = assertThrows(UserExistsException.class,
            () -> userService.createUser("Nikos", "nikos@test.com"));
        assertEquals("A user with an email nikos@test.com already exists", ex.getMessage());
    }

}
