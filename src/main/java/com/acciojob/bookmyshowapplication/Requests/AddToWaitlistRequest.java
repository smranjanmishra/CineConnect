package com.acciojob.bookmyshowapplication.Requests;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request DTO for adding user to waitlist
 */
@Data
public class AddToWaitlistRequest {
    
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobNo;
    
    @NotNull(message = "Theater ID is required")
    @Positive(message = "Theater ID must be positive")
    private Integer theaterId;
    
    @NotBlank(message = "Movie name is required")
    private String movieName;
    
    @NotNull(message = "Show date is required")
    @FutureOrPresent(message = "Show date must be today or in the future")
    private LocalDate showDate;
    
    @NotNull(message = "Show time is required")
    private LocalTime showTime;
    
    @NotBlank(message = "Seat type is required")
    @Pattern(regexp = "^(CLASSIC|PREMIUM)$", message = "Seat type must be CLASSIC or PREMIUM")
    private String requestedSeatType;
    
    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "At least 1 seat required")
    @Max(value = 10, message = "Maximum 10 seats allowed")
    private Integer numberOfSeats;
}
