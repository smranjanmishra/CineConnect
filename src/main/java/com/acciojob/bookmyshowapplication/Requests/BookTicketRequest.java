package com.acciojob.bookmyshowapplication.Requests;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Request DTO for booking tickets
 */
@Data
public class BookTicketRequest {

    @NotBlank(message = "Movie name is required")
    private String movieName;
    
    @NotNull(message = "Show date is required")
    @FutureOrPresent(message = "Show date must be today or in the future")
    private LocalDate showDate;
    
    @NotNull(message = "Show time is required")
    private LocalTime showTime;
    
    @NotEmpty(message = "At least one seat must be requested")
    private List<String> requestedSeats;
    
    @NotNull(message = "Theater ID is required")
    @Positive(message = "Theater ID must be positive")
    private Integer theaterId;
    
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobNo;
}