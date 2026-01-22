package com.acciojob.bookmyshowapplication.Requests;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

/**
 * Request DTO for seat selection
 */
@Data
public class SeatSelectionRequest {
    
    @NotNull(message = "Show ID is required")
    @Positive(message = "Show ID must be positive")
    private Integer showId;
    
    @NotEmpty(message = "At least one seat must be selected")
    private List<String> selectedSeats;
    
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String userMobNo;
}
