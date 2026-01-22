package com.acciojob.bookmyshowapplication.Controllers;

import com.acciojob.bookmyshowapplication.Requests.GetAvailableSeatsRequest;
import com.acciojob.bookmyshowapplication.Requests.SeatSelectionRequest;
import com.acciojob.bookmyshowapplication.Responses.ApiResponse;
import com.acciojob.bookmyshowapplication.Service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for seat selection and availability
 */
@RestController
@RequestMapping("/api/v1/seats")
@Tag(name = "Seat Management", description = "APIs for seat selection and availability")
public class SeatController {
    
    private static final Logger logger = LoggerFactory.getLogger(SeatController.class);
    
    @Autowired
    private SeatService seatService;

    @PostMapping("/available")
    @Operation(summary = "Get available seats", description = "Retrieve available seats for a show with pricing")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAvailableSeats(
            @Valid @RequestBody GetAvailableSeatsRequest request) {
        
        logger.info("Fetching available seats for movie: {} on {} at {}", 
                request.getMovieName(), request.getShowDate(), request.getShowTime());
        
        Map<String, Object> seatLayout = seatService.getAvailableSeatsWithLayout(request);
        return ResponseEntity.ok(ApiResponse.success(seatLayout));
    }

    @PostMapping("/select")
    @Operation(summary = "Select seats", description = "Temporarily select seats for a user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> selectSeats(
            @Valid @RequestBody SeatSelectionRequest request) {
        
        logger.info("User {} selecting seats: {}", request.getUserMobNo(), request.getSelectedSeats());
        
        Map<String, Object> selectionResult = seatService.selectSeats(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Seats selected successfully", selectionResult));
    }

    @PostMapping("/release")
    @Operation(summary = "Release seats", description = "Release temporarily selected seats")
    public ResponseEntity<ApiResponse<String>> releaseSeats(
            @Valid @RequestBody SeatSelectionRequest request) {
        
        logger.info("User {} releasing seats: {}", request.getUserMobNo(), request.getSelectedSeats());
        
        String result = seatService.releaseTemporarySeats(request);
        return ResponseEntity.ok(ApiResponse.success("Seats released successfully", result));
    }
}
