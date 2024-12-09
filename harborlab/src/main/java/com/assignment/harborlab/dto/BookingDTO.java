package com.assignment.harborlab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {

	private Long bookingId;

	private UserDTO userDTO;

	private RoomDTO roomDTO;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private String timeZone;

	public BookingResponse toBookingResponseDto() {
		return BookingResponse.builder()
				.bookingId(bookingId)
				.userEmail(userDTO.getEmail())
				.roomName(roomDTO.getName())
				.startTime(startTime)
				.endTime(endTime)
				.timeZone(timeZone)
				.build();
	}
}

