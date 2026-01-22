package com.acciojob.bookmyshowapplication.Controllers;

import com.acciojob.bookmyshowapplication.Requests.AddToWaitlistRequest;
import com.acciojob.bookmyshowapplication.Responses.ApiResponse;
import com.acciojob.bookmyshowapplication.Responses.WaitlistResponse;
import com.acciojob.bookmyshowapplication.Service.WaitlistService;
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
 * REST Controller for waitlist management
 */
@RestController
@RequestMapping("/api/v1/waitlist")
@Tag(name = "Waitlist Management", description = "APIs for managing show waitlists")
public class WaitlistController {
    
    private static final Logger logger = LoggerFactory.getLogger(WaitlistController.class);

    @Autowired
    private WaitlistService waitlistService;

    @PostMapping
    @Operation(summary = "Add to waitlist", description = "Add user to waitlist when seats are unavailable")
    public ResponseEntity<ApiResponse<WaitlistResponse>> addToWaitlist(
            @Valid @RequestBody AddToWaitlistRequest request) {
        
        logger.info("Adding user {} to waitlist for movie: {}", request.getMobNo(), request.getMovieName());
        
        WaitlistResponse response = waitlistService.addToWaitlist(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Added to waitlist successfully", response));
    }

    @DeleteMapping("/{waitlistId}")
    @Operation(summary = "Cancel waitlist entry", description = "Cancel a user's waitlist entry")
    public ResponseEntity<ApiResponse<String>> cancelWaitlistEntry(@PathVariable Integer waitlistId) {
        logger.info("Cancelling waitlist entry: {}", waitlistId);
        
        waitlistService.cancelWaitlistEntry(waitlistId);
        return ResponseEntity.ok(ApiResponse.success("Waitlist entry cancelled successfully", "Entry cancelled"));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user waitlists", description = "Get all waitlist entries for a user")
    public ResponseEntity<ApiResponse<List<WaitlistResponse>>> getUserWaitlists(@PathVariable Integer userId) {
        logger.info("Fetching waitlists for user: {}", userId);
        
        List<WaitlistResponse> waitlists = waitlistService.getUserWaitlists(userId);
        return ResponseEntity.ok(ApiResponse.success(waitlists));
    }
}
