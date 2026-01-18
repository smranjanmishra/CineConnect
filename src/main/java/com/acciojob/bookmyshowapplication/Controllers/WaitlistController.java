package com.acciojob.bookmyshowapplication.Controllers;

import com.acciojob.bookmyshowapplication.Requests.AddToWaitlistRequest;
import com.acciojob.bookmyshowapplication.Responses.WaitlistResponse;
import com.acciojob.bookmyshowapplication.Service.WaitlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/waitlist")
@Tag(name = "Waitlist Management", description = "APIs for managing show waitlists")
public class WaitlistController {

    @Autowired
    private WaitlistService waitlistService;

    @PostMapping("/add")
    @Operation(summary = "Add to waitlist", description = "Add user to waitlist when seats are unavailable")
    public ResponseEntity<?> addToWaitlist(@RequestBody AddToWaitlistRequest request) {
        try {
            WaitlistResponse response = waitlistService.addToWaitlist(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Error adding to waitlist: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @DeleteMapping("/cancel/{waitlistId}")
    @Operation(summary = "Cancel waitlist entry", description = "Cancel a user's waitlist entry")
    public ResponseEntity<?> cancelWaitlistEntry(@PathVariable Integer waitlistId) {
        try {
            waitlistService.cancelWaitlistEntry(waitlistId);
            return new ResponseEntity<>("Waitlist entry cancelled successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Error cancelling waitlist entry: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user waitlists", description = "Get all waitlist entries for a user")
    public ResponseEntity<?> getUserWaitlists(@PathVariable Integer userId) {
        try {
            List<WaitlistResponse> waitlists = waitlistService.getUserWaitlists(userId);
            return new ResponseEntity<>(waitlists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Error fetching waitlists: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
