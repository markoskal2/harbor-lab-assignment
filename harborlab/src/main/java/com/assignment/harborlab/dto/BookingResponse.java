package com.assignment.harborlab.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

	private Long bookingId;

	private String userEmail;

	private String roomName;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private String timeZone;

}

