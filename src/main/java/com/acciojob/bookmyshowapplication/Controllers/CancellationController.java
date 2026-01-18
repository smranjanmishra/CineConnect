package com.acciojob.bookmyshowapplication.Controllers;

import com.acciojob.bookmyshowapplication.Models.RefundTransaction;
import com.acciojob.bookmyshowapplication.Requests.CancelTicketRequest;
import com.acciojob.bookmyshowapplication.Responses.CancellationResponse;
import com.acciojob.bookmyshowapplication.Service.CancellationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/cancellation")
@Tag(name = "Cancellation & Refund", description = "APIs for ticket cancellation and refund management")
public class CancellationController {

    @Autowired
    private CancellationService cancellationService;

    @PostMapping("/cancel")
    @Operation(summary = "Cancel a ticket", description = "Cancel a booked ticket and process refund based on cancellation policy")
    public ResponseEntity<?> cancelTicket(@RequestBody CancelTicketRequest request) {
        try {
            CancellationResponse response = cancellationService.cancelTicket(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(
                    "Error cancelling ticket: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @GetMapping("/refund-status/{ticketId}")
    @Operation(summary = "Get refund status", description = "Get the refund status for a cancelled ticket")
    public ResponseEntity<?> getRefundStatus(@PathVariable String ticketId) {
        try {
            RefundTransaction refundTransaction = cancellationService.getRefundStatus(ticketId);
            return new ResponseEntity<>(refundTransaction, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(
                    "Error fetching refund status: " + e.getMessage(),
                    HttpStatus.NOT_FOUND
            );
        }
    }
}
