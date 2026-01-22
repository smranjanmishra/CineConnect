package com.acciojob.bookmyshowapplication.Requests;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Request DTO for cancelling tickets
 */
@Data
public class CancelTicketRequest {
    
    @NotBlank(message = "Ticket ID is required")
    private String ticketId;
    
    @Size(max = 500, message = "Cancellation reason must not exceed 500 characters")
    private String cancellationReason;
}
