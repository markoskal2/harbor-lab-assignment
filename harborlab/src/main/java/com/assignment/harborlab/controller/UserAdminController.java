package com.assignment.harborlab.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.harborlab.dto.UserDTO;
import com.assignment.harborlab.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserAdminController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> createRoom(@Valid @RequestBody final UserDTO userCreationRequest) {
        final var user = userService.createUser(userCreationRequest.getName(), userCreationRequest.getEmail());

        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<UserDTO> findUserByEmail(@RequestParam final String email) {
        final var user = userService.findUserByEmail(email);

        return ResponseEntity.ok(user);
    }
}