package com.assignment.harborlab.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.harborlab.dto.RoomDTO;
import com.assignment.harborlab.service.RoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/room")
public class RoomAdminController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@RequestParam final String name) {
        final var room = roomService.createRoom(name);

        return ResponseEntity.ok(room);
    }

    @GetMapping
    public ResponseEntity<RoomDTO> findRoomByName(@RequestParam final String name) {
        final var room = roomService.findRoom(name);

        return ResponseEntity.ok(room);
    }
}