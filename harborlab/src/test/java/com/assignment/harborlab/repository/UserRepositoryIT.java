package com.assignment.harborlab.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.assignment.harborlab.entity.User;

@DataJpaTest
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindById() {
        final var user = User.builder()
            .name("markos_kal")
            .email("markos.kal@test.com")
            .build();

        final var savedUser = userRepository.save(user);
        final var retrievedUser = userRepository.findById(savedUser.getId());

        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getName()).isEqualTo(user.getName());
        assertThat(retrievedUser.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void testFindByEmail() {
        final var user = User.builder()
            .name("markos_kal")
            .email("markos.kal@test.com")
            .build();
        userRepository.save(user);

        final var retrievedUser = userRepository.findByEmail(user.getEmail());

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getEmail()).isEqualTo(user.getEmail());
    }
}