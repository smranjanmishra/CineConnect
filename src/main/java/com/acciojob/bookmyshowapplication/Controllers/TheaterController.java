package com.acciojob.bookmyshowapplication.Controllers;

import com.acciojob.bookmyshowapplication.Models.Theater;
import com.acciojob.bookmyshowapplication.Requests.AddTheaterRequest;
import com.acciojob.bookmyshowapplication.Requests.AddTheaterSeatsRequest;
import com.acciojob.bookmyshowapplication.Responses.ApiResponse;
import com.acciojob.bookmyshowapplication.Service.TheaterService;
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
 * REST Controller for theater management operations
 */
@RestController
@RequestMapping("/api/v1/theaters")
@Tag(name = "Theater Management", description = "APIs for managing theaters and their seats")
public class TheaterController {
    
    private static final Logger logger = LoggerFactory.getLogger(TheaterController.class);

    @Autowired
    private TheaterService theaterService;

    @PostMapping
    @Operation(summary = "Add theater", description = "Register a new theater in the system")
    public ResponseEntity<ApiResponse<String>> addTheater(@Valid @RequestBody AddTheaterRequest request) {
        logger.info("Adding new theater: {}", request.getName());
        
        String result = theaterService.addTheater(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Theater added successfully", result));
    }

    @PostMapping("/{theaterId}/seats")
    @Operation(summary = "Add theater seats", description = "Add seats to a theater")
    public ResponseEntity<ApiResponse<String>> addTheaterSeats(
            @PathVariable Integer theaterId,
            @Valid @RequestBody AddTheaterSeatsRequest request) {
        
        logger.info("Adding seats to theater ID: {}", theaterId);
        
        String result = theaterService.addTheaterSeats(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Theater seats added successfully", result));
    }
    
    @GetMapping
    @Operation(summary = "Get all theaters", description = "Retrieve list of all theaters")
    public ResponseEntity<ApiResponse<List<Theater>>> getAllTheaters() {
        logger.info("Fetching all theaters");
        
        List<Theater> theaters = theaterService.getAllTheaters();
        return ResponseEntity.ok(ApiResponse.success(theaters));
    }
    
    @GetMapping("/{theaterId}")
    @Operation(summary = "Get theater by ID", description = "Retrieve theater details by ID")
    public ResponseEntity<ApiResponse<Theater>> getTheaterById(@PathVariable Integer theaterId) {
        logger.info("Fetching theater with ID: {}", theaterId);
        
        Theater theater = theaterService.getTheaterById(theaterId);
        return ResponseEntity.ok(ApiResponse.success(theater));
    }
}
