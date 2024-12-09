package com.assignment.harborlab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.assignment.harborlab.exception.RoomExistsException;
import com.assignment.harborlab.repository.RoomRepository;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RoomServiceIT {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void testCreateRoom() {
        final var room = roomService.createRoom("Amsterdam");

        final var retrievedRoom = roomRepository.findRoomByName(room.getName());

        assertThat(room.getName()).isNotNull();
        assertThat(room.getName()).isEqualTo(retrievedRoom.getName());
    }

    @Test
    void testDuplicateRoomNameThrowsException() {
        roomService.createRoom("Amsterdam");

        final var ex = assertThrows(RoomExistsException.class,
            () -> roomService.createRoom("Amsterdam"));
        assertEquals("A room named Amsterdam already exists", ex.getMessage());
    }

}
