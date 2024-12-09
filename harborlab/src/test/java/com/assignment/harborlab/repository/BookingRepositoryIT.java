package com.assignment.harborlab.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.assignment.harborlab.entity.Booking;
import com.assignment.harborlab.entity.Room;
import com.assignment.harborlab.entity.User;

@DataJpaTest
class BookingRepositoryIT {

    public static final String TIME_ZONE = "Europe/Amsterdam";

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateBooking() {
        // given
        final var room = Room.builder()
            .name("Amsterdam")
            .build();
        final var savedRoom = roomRepository.save(room);

        final var user = User.builder()
            .name("markos_kal")
            .email("markos.kal@test.com")
            .build();
        final var savedUser = userRepository.save(user);

        // when
        final var now = LocalDateTime.now();

        final var booking = Booking.builder()
            .user(savedUser)
            .room(savedRoom)
            .utcStartTime(now.plusDays(5))
            .utcEndTime(now.plusDays(5).plusHours(1))
            .timeZone(TIME_ZONE)
            .build();

        final var savedBooking = bookingRepository.save(booking);
        final var retrievedBooking = bookingRepository.findById(savedBooking.getId()).get();

        // Assert
        assertThat(savedBooking.getId()).isEqualTo(retrievedBooking.getId());
    }

    @Test
    void testRetrieveBookings() {
        final var room = Room.builder()
            .name("Amsterdam")
            .build();
        final var savedRoom = roomRepository.save(room);

        final var user = User.builder()
            .name("markos_kal")
            .email("markos.kal@test.com")
            .build();

        final var savedUser = userRepository.save(user);

        final var now = LocalDateTime.now();
        final var dateNow = LocalDate.now();

        final var booking = Booking.builder()
            .user(savedUser)
            .room(savedRoom)
            .utcStartTime(now.plusDays(3))
            .utcEndTime(now.plusDays(3).plusHours(1))
            .timeZone(TIME_ZONE)
            .build();
        bookingRepository.save(booking);

        final var booking1 = Booking.builder()
            .user(savedUser)
            .room(savedRoom)
            .utcStartTime(now.plusDays(3).plusHours(2))
            .utcEndTime(now.plusDays(3).plusHours(3))
            .timeZone(TIME_ZONE)
            .build();
        bookingRepository.save(booking1);

        final var booking2 = Booking.builder()
            .user(savedUser)
            .room(savedRoom)
            .utcStartTime(now.plusDays(30).plusHours(2))
            .utcEndTime(now.plusDays(30).plusHours(3))
            .timeZone(TIME_ZONE)
            .build();
        bookingRepository.save(booking2);

        final var bookings = bookingRepository.findBookingsForDate(savedRoom,
            dateNow.plusDays(3).atStartOfDay(),
            dateNow.plusDays(4).atStartOfDay());

        // Assert
        assertThat(bookings.size()).isEqualTo(2);
    }

}