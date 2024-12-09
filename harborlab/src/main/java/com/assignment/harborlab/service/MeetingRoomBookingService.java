package com.assignment.harborlab.service;

import static com.assignment.harborlab.utils.Constants.BOOKING_CANCELLED;
import static com.assignment.harborlab.utils.Constants.BOOKING_NOT_FOUND_MESSAGE;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.assignment.harborlab.assembler.AcmeAssembler;
import com.assignment.harborlab.dto.BookingDTO;
import com.assignment.harborlab.entity.Booking;
import com.assignment.harborlab.entity.Room;
import com.assignment.harborlab.exception.BookingConflictException;
import com.assignment.harborlab.exception.BookingTimesNotValidException;
import com.assignment.harborlab.exception.RoomNotFoundException;
import com.assignment.harborlab.exception.UserDoesNotExistException;
import com.assignment.harborlab.exception.ZoneIdInvalidException;
import com.assignment.harborlab.repository.BookingRepository;
import com.assignment.harborlab.repository.RoomRepository;
import com.assignment.harborlab.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetingRoomBookingService {

	private final RoomRepository roomRepository;
	private final UserRepository userRepository;
	private final BookingRepository bookingRepository;
	private final AcmeAssembler acmeAssembler;

	public BookingDTO createBooking(
			final String userEmail,
			final String roomName,
			final LocalDate date,
			final LocalTime startTime,
			final LocalTime endTime,
			final String zoneId
	) {
		validateDates(date, startTime, endTime, zoneId);

		final var room = roomRepository.findRoomByName(roomName);
		if (Objects.isNull(room)) {
			throw new RoomNotFoundException("Room with name " + roomName + " does not exist");
		}

		final var user = userRepository.findByEmail(userEmail);
		if (Objects.isNull(user)) {
			throw new UserDoesNotExistException("User with email " + userEmail + " does not exist");
		}

		final var utcDateStartTime = getZonedDateTimeUTC(date, startTime, zoneId);
		final var utcDateEndTime = getZonedDateTimeUTC(date, endTime, zoneId);

		validateNoConflicts(room, utcDateStartTime, utcDateEndTime);

		final var booking = Booking.builder()
				.user(user)
				.room(room)
				.utcStartTime(utcDateStartTime.toLocalDateTime())
				.utcEndTime(utcDateEndTime.toLocalDateTime())
				.timeZone(zoneId)
				.build();

		final var savedEntity = bookingRepository.save(booking);

		return acmeAssembler.assembleBooking(savedEntity);
	}

	private void validateDates(final LocalDate date,
			final LocalTime startTime,
			final LocalTime endTime,
			final String zone) {
		// this is to satisfy the constraint of the assignment that a room can be booked for at least 1 hour and multiples of 1 hour
		if (Duration.between(startTime, endTime).toMinutes() % 60 != 0) {
			throw new BookingTimesNotValidException("Meeting duration needs to be multiples of 1 hour");
		}

		final var utcDateStartTime = getZonedDateTimeUTC(date, startTime, zone);
		final var utcDateEndTime = getZonedDateTimeUTC(date, endTime, zone);

		if (utcDateStartTime.isAfter(utcDateEndTime)) {
			throw new BookingTimesNotValidException("Start time is after end time");
		}
		if (utcDateStartTime.isBefore(ZonedDateTime.now(ZoneId.of("UTC")))) {
			throw new BookingTimesNotValidException("Start time is in the past");
		}
	}

	private void validateNoConflicts(final Room room,
			final ZonedDateTime utcDateStartTime,
			final ZonedDateTime utcDateEndTime) {

		final var bookings = bookingRepository.findBookingsForDate(room, utcDateStartTime.toLocalDateTime(), utcDateEndTime.toLocalDateTime());
		if (!bookings.isEmpty()) {
			throw new BookingConflictException(String.format("Room %s is unavailable for the desired timeframe", room.getName()));
		}
	}

	private ZonedDateTime getZonedDateTimeUTC(final LocalDate date,
			final LocalTime time,
			final String zone) {
		// convert dates to a ZonedDateTime
		final ZoneId zoneId;
		try {
			zoneId = ZoneId.of(zone);
		} catch (final DateTimeException ex) {
			throw new ZoneIdInvalidException(String.format("ZoneId: %s is not a valid ZoneId", zone));
		}

		// Combine LocalDate and LocalTime into LocalDateTime
		final var localDateTime = LocalDateTime.of(date, time);

		// Convert LocalDateTime to ZonedDateTime using the ZoneId
		final var zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);

		// Convert to UTC so that we can have a common point of reference
		return zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
	}

	public List<BookingDTO> findBookingsOfRoomOnDate(final String roomName, final LocalDate date) {

		final var room = roomRepository.findRoomByName(roomName);
		if (Objects.isNull(room)) {
			throw new RoomNotFoundException(String.format("Room with name %s does not exist",  roomName));
		}

		final var bookingsForDate = bookingRepository.findBookingsForDate(room,
				date.atStartOfDay(),
				date.plusDays(1).atStartOfDay());

		return bookingsForDate.stream()
				.map(acmeAssembler::assembleBooking)
				.collect(Collectors.toList());
	}

	public String cancelBooking(final String id) {

		final var dbId = Long.parseLong(id);
		final var bookExists = bookingRepository.existsById(dbId);

		if (bookExists) {
			bookingRepository.deleteById(dbId);
			return BOOKING_CANCELLED;
		}

		return String.format(BOOKING_NOT_FOUND_MESSAGE, id);
	}
}
