package com.assignment.harborlab.assembler;

import java.time.ZoneId;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.assignment.harborlab.dto.BookingDTO;
import com.assignment.harborlab.dto.RoomDTO;
import com.assignment.harborlab.dto.UserDTO;
import com.assignment.harborlab.entity.Booking;
import com.assignment.harborlab.entity.Room;
import com.assignment.harborlab.entity.User;

@Component
public class AcmeAssembler {

	public UserDTO assembleUser(final User user) {

		final var name = Optional.of(user)
				.map(User::getName)
				.orElse(null);

		final var email = Optional.of(user)
				.map(User::getEmail)
				.orElse(null);

		return UserDTO.builder()
				.name(name)
				.email(email)
				.build();
	}

	public BookingDTO assembleBooking(final Booking booking) {

		final var id = Optional.of(booking)
				.map(Booking::getId)
				.orElse(null);

		final var user = Optional.of(booking)
				.map(Booking::getUser)
				.orElse(null);

		final var room = Optional.of(booking)
				.map(Booking::getRoom)
				.orElse(null);

		final var utcStartTime = Optional.of(booking)
				.map(Booking::getUtcStartTime)
				.orElse(null);

		final var utcEndTime = Optional.of(booking)
				.map(Booking::getUtcEndTime)
				.orElse(null);

		final var timeZone = Optional.of(booking)
				.map(Booking::getTimeZone)
				.orElse(null);

		final var zoneId = ZoneId.of("UTC");
		final var startTime = utcStartTime.atZone(zoneId)
				.withZoneSameInstant(ZoneId.of(timeZone))
				.toLocalDateTime();

		final var endTime = utcEndTime.atZone(zoneId)
				.withZoneSameInstant(ZoneId.of(timeZone))
				.toLocalDateTime();

		return BookingDTO.builder()
				.bookingId(id)
				.userDTO(assembleUser(user))
				.roomDTO(assembleRoomDTO(room))
				.startTime(startTime)
				.endTime(endTime)
				.timeZone(timeZone)
				.build();
	}

	public RoomDTO assembleRoomDTO(final Room room) {

		final var name = Optional.of(room)
				.map(Room::getName)
				.orElse(null);

		return RoomDTO.builder()
				.name(name)
				.build();
	}
}
