package com.assignment.harborlab.service;

import org.springframework.stereotype.Service;

import com.assignment.harborlab.assembler.AcmeAssembler;
import com.assignment.harborlab.dto.RoomDTO;
import com.assignment.harborlab.entity.Room;
import com.assignment.harborlab.exception.RoomExistsException;
import com.assignment.harborlab.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final AcmeAssembler acmeAssembler;

    public RoomDTO createRoom(final String name) {

        final var roomByName = roomRepository.findRoomByName(name);
        if (roomByName != null) {
					throw new RoomExistsException(String.format("A room named %s already exists", name));
				}
        final var room = Room.builder()
            .name(name)
            .build();

        final var saveRoom = roomRepository.save(room);
        return acmeAssembler.assembleRoomDTO(saveRoom);
    }

    public RoomDTO findRoom(final String name) {

        final var roomByName = roomRepository.findRoomByName(name);
        return acmeAssembler.assembleRoomDTO(roomByName);
    }
}
