package com.assignment.harborlab.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.harborlab.dto.BookingDTO;
import com.assignment.harborlab.dto.BookingRequest;
import com.assignment.harborlab.dto.BookingResponse;
import com.assignment.harborlab.service.MeetingRoomBookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/booking")
public class MeetingRoomBookingController {

	private final MeetingRoomBookingService bookingService;

	@PostMapping
	public ResponseEntity<BookingResponse> createBooking(
			@Valid @RequestBody final BookingRequest bookingRequest) {
		final var startTime = getLocalTimeFromInput(bookingRequest.getStartHour(), bookingRequest.getStartMinute());
		final var endTime = getLocalTimeFromInput(bookingRequest.getEndHour(), bookingRequest.getEndMinute());

		final var booking = bookingService.createBooking(
				bookingRequest.getUserEmail(),
				bookingRequest.getRoomName(),
				bookingRequest.getDate(),
				startTime,
				endTime,
				bookingRequest.getZoneId()
		);

		return ResponseEntity.ok(booking.toBookingResponseDto());
	}

	@GetMapping
	public ResponseEntity<List<BookingResponse>> retrieveBookingsForRoomOnDate(
			@RequestParam final String roomName,
			@RequestParam final LocalDate date
	) {

		final var findBookingsFromDb = bookingService.findBookingsOfRoomOnDate(roomName, date);
		final var bookings = findBookingsFromDb.stream()
				.map(BookingDTO::toBookingResponseDto)
				.collect(Collectors.toList());

		return ResponseEntity.ok(bookings);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> cancelBooking(@PathVariable final String id) {

		final var result = bookingService.cancelBooking(id);

		return ResponseEntity.ok(result);
	}

	private LocalTime getLocalTimeFromInput(final Integer hour, final Integer minute) {

		try {
			return LocalTime.of(hour, minute);
		} catch (final Exception ex) {
			throw new IllegalArgumentException("Invalid hour and/or minute format", ex);
		}
	}
}

