package com.assignment.harborlab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.assignment.harborlab.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Room findRoomByName(String name);

}