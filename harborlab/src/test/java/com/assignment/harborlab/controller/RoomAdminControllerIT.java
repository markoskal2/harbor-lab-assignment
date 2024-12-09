package com.assignment.harborlab.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.assignment.harborlab.dto.RoomDTO;
import com.assignment.harborlab.exception.RoomExistsException;
import com.assignment.harborlab.service.RoomService;

@SpringBootTest
@AutoConfigureMockMvc
public class RoomAdminControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @Test
    void testCreateRoom() throws Exception {

        final var room = RoomDTO.builder()
            .name("Amsterdam")
            .build();
        when(roomService.createRoom("Amsterdam")).thenReturn(room);

        mockMvc.perform(MockMvcRequestBuilders.post("/room")
                        .param("name", "Amsterdam")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());

        verify(roomService, times(1)).createRoom("Amsterdam");
    }

    @Test
    void testBadRequestReturnedWhenRoomAlreadyExists() throws Exception {
        when(roomService.createRoom("Athens")).thenThrow(new RoomExistsException("RoomExists"));

        mockMvc.perform(MockMvcRequestBuilders.post("/room")
                        .param("name", "Athens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("RoomExists"));
    }

}
