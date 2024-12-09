package com.assignment.harborlab.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.assignment.harborlab.dto.UserDTO;
import com.assignment.harborlab.exception.UserExistsException;
import com.assignment.harborlab.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class UserAdminControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();  // Jackson ObjectMapper to convert objects to JSON
    }

    @Test
    void testCreateUser() throws Exception {

        final var user = UserDTO.builder()
            .name("Markos")
            .email("markos@test.com")
            .build();
            
        when(userService.createUser("Markos", "markos@test.com")).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());

        verify(userService, times(1)).createUser("Markos", "markos@test.com");
    }

    @Test
    void testBadRequestReturnedWhenUserEmailAlreadyExists() throws Exception {
        final var user = UserDTO.builder()
            .name("Nikos")
            .email("Nikos@test.com")
            .build();

        when(userService.createUser("Nikos", "nikos@test.com"))
            .thenThrow(new UserExistsException("UserExists"));

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("UserExists"));
    }

    @Test
    void testBadRequestReturnedWhenEmailIsNotValid() throws Exception {
        final var user = UserDTO.builder()
            .name("Markos")
            .email("wrong_email")
            .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"email\":\"Invalid email format\"}"));
    }

}
