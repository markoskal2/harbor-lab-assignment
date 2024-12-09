package com.assignment.harborlab.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.assignment.harborlab.entity.Booking;
import com.assignment.harborlab.entity.Room;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Custom query to find bookings that overlap with a given LocalDate
    @Query("SELECT b FROM Booking b WHERE " +
            "(b.utcStartTime < :endOfDay AND b.utcEndTime >= :startOfDay AND b.room = :room)")
    List<Booking> findBookingsForDate(Room room, LocalDateTime startOfDay, LocalDateTime endOfDay);


}
