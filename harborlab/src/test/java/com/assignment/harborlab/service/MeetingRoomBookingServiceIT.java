package com.assignment.harborlab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.assignment.harborlab.exception.BookingConflictException;
import com.assignment.harborlab.exception.BookingTimesNotValidException;
import com.assignment.harborlab.exception.RoomNotFoundException;
import com.assignment.harborlab.exception.UserDoesNotExistException;
import com.assignment.harborlab.exception.ZoneIdInvalidException;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MeetingRoomBookingServiceIT {

	public static final String TIME_ZONE_AMS = "Europe/Amsterdam";
	public static final String TIME_ZONE_ATH = "Europe/Athens";

	@Autowired
	private MeetingRoomBookingService meetingRoomBookingService;

	@Autowired
	private RoomService roomService;

	@Autowired
	private UserService userService;

	@Test
	void testCreateBookingForRoom() {
		final var room = roomService.createRoom("Amsterdam");
		final var user = userService.createUser("Markos",
				UUID.randomUUID()
						.toString()
						.replace("-", "") + "@test.com");

		final var bookingDate = LocalDate.now()
				.plusDays(3);
		final var booking = meetingRoomBookingService.createBooking(
				user.getEmail(),
				room.getName(),
				bookingDate,
				LocalTime.of(12, 0),
				LocalTime.of(13, 0),
				TIME_ZONE_AMS);

		assertThat(booking.getRoomDTO().getName()).isEqualTo(room.getName());
		assertThat(booking.getUserDTO().getEmail()).isEqualTo(user.getEmail());
		assertThat(booking.getTimeZone()).isEqualTo(TIME_ZONE_AMS);
		assertThat(booking.getStartTime()).isEqualTo(LocalDateTime.of(bookingDate, LocalTime.of(12, 0)));
		assertThat(booking.getEndTime()).isEqualTo(LocalDateTime.of(bookingDate, LocalTime.of(13, 0)));
	}

	@Test
	void testCreateOverlappingBookingThrowsException() {
		// given
		final var room = roomService.createRoom("Amsterdam");
		final var user = userService.createUser("Markos",
				UUID.randomUUID()
						.toString()
						.replace("-", "") + "@test.com");

		final var bookingDate = LocalDate.now()
				.plusDays(3);

		meetingRoomBookingService.createBooking(user.getEmail(),
				room.getName(),
				bookingDate,
				LocalTime.of(12, 0),
				LocalTime.of(13, 0), TIME_ZONE_AMS);

		// when
		assertThatThrownBy(() -> meetingRoomBookingService.createBooking(user.getEmail(),
				room.getName(),
				bookingDate,
				LocalTime.of(12, 0),
				LocalTime.of(13, 0), TIME_ZONE_AMS)).isInstanceOf(BookingConflictException.class)
				.hasMessage("Room Amsterdam is unavailable for the desired timeframe");
	}

	@Test
	void testCreateOverlappingBookingOnDifferentTimezonesThrowsException() {
		// given
		final var room = roomService.createRoom("Amsterdam");
		final var user = userService.createUser("Markos",
				UUID.randomUUID()
						.toString()
						.replace("-", "") + "@test.com");

		final var bookingDate = LocalDate.now()
				.plusDays(3);
		meetingRoomBookingService.createBooking(user.getEmail(),
				room.getName(),
				bookingDate,
				LocalTime.of(12, 0),
				LocalTime.of(13, 0), TIME_ZONE_AMS);

		// when
		assertThatThrownBy(() -> meetingRoomBookingService.createBooking(user.getEmail(),
				room.getName(),
				bookingDate,
				LocalTime.of(13, 0),
				LocalTime.of(14, 0),
				TIME_ZONE_ATH))
				.isInstanceOf(BookingConflictException.class)
				.hasMessage("Room Amsterdam is unavailable for the desired timeframe");
	}

	@Test
	void testCreateNoMultiplesOf60MinutesBookingsThrowsException() {
		// given
		final var room = roomService.createRoom("Amsterdam");
		final var user = userService.createUser("Markos",
				UUID.randomUUID()
						.toString()
						.replace("-", "") + "@test.com");

		final var bookingDate = LocalDate.now()
				.plusDays(3);

		// when
		assertThatThrownBy(() -> meetingRoomBookingService.createBooking(user.getEmail(),
				room.getName(),
				bookingDate,
				LocalTime.of(13, 0),
				LocalTime.of(14, 52),
				TIME_ZONE_ATH)).isInstanceOf(BookingTimesNotValidException.class)
				.hasMessage("Meeting duration needs to be multiples of 1 hour");
	}

	@Test
	void testCreateInvalidZoneIdThrowsException() {
		// given
		final var room = roomService.createRoom("Amsterdam");
		final var user = userService.createUser("Markos",
				UUID.randomUUID()
						.toString()
						.replace("-", "") + "@test.com");

		final var bookingDate = LocalDate.now()
				.plusDays(3);

		// when
		assertThatThrownBy(() -> meetingRoomBookingService.createBooking(user.getEmail(),
				room.getName(),
				bookingDate,
				LocalTime.of(13, 0),
				LocalTime.of(14, 00),
				"Eu/ens")).isInstanceOf(ZoneIdInvalidException.class)
				.hasMessage("ZoneId: Eu/ens is not a valid ZoneId");
	}

	@Test
	void testCreateBookingIsInThePastThrowsException() {
		// given
		final var room = roomService.createRoom("Amsterdam");
		final var user = userService.createUser("Markos",
				UUID.randomUUID()
						.toString()
						.replace("-", "") + "@test.com");

		final var bookingDate = LocalDate.now()
				.minusDays(3);

		// when
		assertThatThrownBy(() -> meetingRoomBookingService.createBooking(user.getEmail(),
				room.getName(),
				bookingDate,
				LocalTime.of(13, 0),
				LocalTime.of(14, 00),
				TIME_ZONE_ATH)).isInstanceOf(BookingTimesNotValidException.class)
				.hasMessage("Start time is in the past");
	}

	@Test
	void testCreateBookingStartIsAfterEndThrowsException() {
		// given
		final var room = roomService.createRoom("Amsterdam");
		final var user = userService.createUser("Markos",
				UUID.randomUUID()
						.toString()
						.replace("-", "") + "@test.com");

		final var bookingDate = LocalDate.now()
				.minusDays(3);

		// when
		assertThatThrownBy(() -> meetingRoomBookingService.createBooking(user.getEmail(),
				room.getName(),
				bookingDate,
				LocalTime.of(15, 0),
				LocalTime.of(14, 0),
				TIME_ZONE_ATH)).isInstanceOf(BookingTimesNotValidException.class)
				.hasMessage("Start time is after end time");
	}

	@Test
	void testCreateBookingRoomDoesNotExistThrowsException() {
		// given
		final var user = userService.createUser("Markos",
				UUID.randomUUID()
						.toString()
						.replace("-", "") + "@test.com");
		final var bookingDate = LocalDate.now()
				.plusDays(3);

		// when
		assertThatThrownBy(() -> meetingRoomBookingService.createBooking(user.getEmail(),
				"wrong_room",
				bookingDate,
				LocalTime.of(14, 0),
				LocalTime.of(15, 0),
				TIME_ZONE_ATH)).isInstanceOf(RoomNotFoundException.class)
				.hasMessage("Room with name wrong_room does not exist");
	}

	@Test
	void testCreateBookingUserDoesNotExistThrowsException() {
		// given
		final var room = roomService.createRoom("Amsterdam");
		final var bookingDate = LocalDate.now()
				.plusDays(3);

		// when
		assertThatThrownBy(() -> meetingRoomBookingService.createBooking("tesdah@test.com",
				room.getName(),
				bookingDate,
				LocalTime.of(14, 0),
				LocalTime.of(15, 0),
				TIME_ZONE_ATH)).isInstanceOf(UserDoesNotExistException.class)
				.hasMessage("User with email tesdah@test.com does not exist");
	}

	@Test
	void testRetrieveBookingOfRoomOnDate() {
		// given
		final var room = roomService.createRoom(UUID.randomUUID()
				.toString()
				.replace("-", ""));
		final var newRoom = roomService.createRoom(UUID.randomUUID()
				.toString()
				.replace("-", ""));
		final var user = userService.createUser("Markos",
				UUID.randomUUID()
						.toString()
						.replace("-", "") + "@test.com");

		final var bookingDate = LocalDate.now()
				.plusDays(3);
		meetingRoomBookingService.createBooking(user.getEmail(),
				room.getName(),
				bookingDate,
				LocalTime.of(12, 0),
				LocalTime.of(13, 0),
				TIME_ZONE_AMS);

		meetingRoomBookingService.createBooking(user.getEmail(),
				room.getName(),
				bookingDate,
				LocalTime.of(14, 0),
				LocalTime.of(16, 0),
				TIME_ZONE_AMS);

		meetingRoomBookingService.createBooking(user.getEmail(),
				newRoom.getName(),
				bookingDate,
				LocalTime.of(14, 0),
				LocalTime.of(16, 0),
				TIME_ZONE_AMS);

		meetingRoomBookingService.createBooking(user.getEmail(),
				room.getName(),
				bookingDate.plusDays(4),
				LocalTime.of(14, 0),
				LocalTime.of(16, 0),
				TIME_ZONE_AMS);

		// when
		final var bookings = meetingRoomBookingService.findBookingsOfRoomOnDate(room.getName(), bookingDate);

		assertThat(bookings).hasSize(2);
	}

	@Test
	void testCancellationOfBooking() {
		// given
		final var room = roomService.createRoom(UUID.randomUUID()
				.toString()
				.replace("-", ""));
		final var user = userService.createUser("Markos",
				UUID.randomUUID()
						.toString()
						.replace("-", "") + "@test.com");

		final var bookingDate = LocalDate.now()
				.plusDays(3);
		final var booking = meetingRoomBookingService.createBooking(user.getEmail(),
				room.getName(),
				bookingDate,
				LocalTime.of(12, 0),
				LocalTime.of(13, 0),
				TIME_ZONE_AMS);

		// when
		final var bookingId = String.valueOf(booking.getBookingId());
		final var response = meetingRoomBookingService.cancelBooking(bookingId);
		assertThat(response).isEqualTo("Booking has been cancelled");

		final var responseNonExistent = meetingRoomBookingService.cancelBooking("21512212");
		assertThat(responseNonExistent).isEqualTo("No booking with id 21512212 exists");
	}
}
