package com.assignment.harborlab.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.assignment.harborlab.entity.Room;

@DataJpaTest
class RoomRepositoryIT {

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void testSaveAndFindById() {
        final var room = Room.builder()
            .name("Amsterdam")
            .build();

        final var savedRoom = roomRepository.save(room);
        final var retrievedUser = roomRepository.findById(savedRoom.getId());

        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getName()).isEqualTo(room.getName());
    }

    @Test
    void testFindByName() {
        final var room = Room.builder()
            .name("Athens")
            .build();
        roomRepository.save(room);

        final var retrievedRoom = roomRepository.findRoomByName(room.getName());

        assertThat(retrievedRoom).isNotNull();
        assertThat(retrievedRoom.getName()).isEqualTo(room.getName());
    }
}