package com.acciojob.bookmyshowapplication.Controllers;

import com.acciojob.bookmyshowapplication.Models.RefundTransaction;
import com.acciojob.bookmyshowapplication.Requests.CancelTicketRequest;
import com.acciojob.bookmyshowapplication.Responses.ApiResponse;
import com.acciojob.bookmyshowapplication.Responses.CancellationResponse;
import com.acciojob.bookmyshowapplication.Service.CancellationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for ticket cancellation and refund management
 */
@RestController
@RequestMapping("/api/v1/tickets")
@Tag(name = "Cancellation & Refund", description = "APIs for ticket cancellation and refund management")
public class CancellationController {
    
    private static final Logger logger = LoggerFactory.getLogger(CancellationController.class);

    @Autowired
    private CancellationService cancellationService;

    @PostMapping("/{ticketId}/cancel")
    @Operation(summary = "Cancel ticket", description = "Cancel a booked ticket and process refund based on cancellation policy")
    public ResponseEntity<ApiResponse<CancellationResponse>> cancelTicket(
            @PathVariable String ticketId,
            @Valid @RequestBody CancelTicketRequest request) {
        
        logger.info("Cancelling ticket: {}", ticketId);
        request.setTicketId(ticketId);
        
        CancellationResponse response = cancellationService.cancelTicket(request);
        return ResponseEntity.ok(ApiResponse.success("Ticket cancelled successfully", response));
    }

    @GetMapping("/{ticketId}/refund")
    @Operation(summary = "Get refund status", description = "Get the refund status for a cancelled ticket")
    public ResponseEntity<ApiResponse<RefundTransaction>> getRefundStatus(@PathVariable String ticketId) {
        logger.info("Fetching refund status for ticket: {}", ticketId);
        
        RefundTransaction refundTransaction = cancellationService.getRefundStatus(ticketId);
        return ResponseEntity.ok(ApiResponse.success(refundTransaction));
    }
}
