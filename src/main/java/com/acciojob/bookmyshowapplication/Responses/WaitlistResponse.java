package com.acciojob.bookmyshowapplication.Responses;

import com.acciojob.bookmyshowapplication.Enums.WaitlistStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WaitlistResponse {
    private Integer waitlistId;
    private WaitlistStatus status;
    private String movieName;
    private String theaterName;
    private String showDateTime;
    private String requestedSeatType;
    private Integer numberOfSeats;
    private Integer positionInQueue;
    private LocalDateTime createdAt;
    private String message;
}
