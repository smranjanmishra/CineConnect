package com.acciojob.bookmyshowapplication.Controllers;

import com.acciojob.bookmyshowapplication.Models.Ticket;
import com.acciojob.bookmyshowapplication.Requests.BookTicketRequest;
import com.acciojob.bookmyshowapplication.Responses.ApiResponse;
import com.acciojob.bookmyshowapplication.Service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for ticket booking and management
 */
@RestController
@RequestMapping("/api/v1/tickets")
@Tag(name = "Ticket Management", description = "APIs for booking and managing tickets")
public class TicketController {
    
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    @Autowired
    private TicketService ticketService;

    @PostMapping("/book")
    @Operation(summary = "Book ticket", description = "Book tickets for a movie show")
    public ResponseEntity<ApiResponse<Ticket>> bookTicket(@Valid @RequestBody BookTicketRequest request) {
        logger.info("Booking ticket for movie: {} by user: {}", request.getMovieName(), request.getMobNo());
        
        Ticket ticket = ticketService.bookTicket(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Ticket booked successfully", ticket));
    }
    
    @GetMapping("/{ticketId}")
    @Operation(summary = "Get ticket details", description = "Retrieve ticket details by ticket ID")
    public ResponseEntity<ApiResponse<Ticket>> getTicketById(@PathVariable String ticketId) {
        logger.info("Fetching ticket with ID: {}", ticketId);
        
        Ticket ticket = ticketService.getTicketById(ticketId);
        return ResponseEntity.ok(ApiResponse.success(ticket));
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user tickets", description = "Retrieve all tickets for a user")
    public ResponseEntity<ApiResponse<List<Ticket>>> getUserTickets(@PathVariable Integer userId) {
        logger.info("Fetching tickets for user ID: {}", userId);
        
        List<Ticket> tickets = ticketService.getUserTickets(userId);
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }
}
