package com.acciojob.bookmyshowapplication.Controllers;

import com.acciojob.bookmyshowapplication.Models.Show;
import com.acciojob.bookmyshowapplication.Requests.AddShowRequest;
import com.acciojob.bookmyshowapplication.Requests.AddShowSeatsRequest;
import com.acciojob.bookmyshowapplication.Responses.ApiResponse;
import com.acciojob.bookmyshowapplication.Service.ShowService;
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
 * REST Controller for show management operations
 */
@RestController
@RequestMapping("/api/v1/shows")
@Tag(name = "Show Management", description = "APIs for managing movie shows")
public class ShowController {
    
    private static final Logger logger = LoggerFactory.getLogger(ShowController.class);
    
    @Autowired
    private ShowService showService;

    @PostMapping
    @Operation(summary = "Create show", description = "Schedule a new movie show")
    public ResponseEntity<ApiResponse<String>> createShow(@Valid @RequestBody AddShowRequest request) {
        logger.info("Creating new show for movie: {}", request.getMovieName());
        
        String result = showService.addShows(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Show created successfully", result));
    }

    @PostMapping("/{showId}/seats")
    @Operation(summary = "Add show seats", description = "Initialize seats for a show")
    public ResponseEntity<ApiResponse<String>> addShowSeats(
            @PathVariable Integer showId,
            @Valid @RequestBody AddShowSeatsRequest request) {
        
        logger.info("Adding seats to show ID: {}", showId);
        
        String response = showService.addShowSeats(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Show seats added successfully", response));
    }
    
    @GetMapping("/{showId}")
    @Operation(summary = "Get show by ID", description = "Retrieve show details by ID")
    public ResponseEntity<ApiResponse<Show>> getShowById(@PathVariable Integer showId) {
        logger.info("Fetching show with ID: {}", showId);
        
        Show show = showService.getShowById(showId);
        return ResponseEntity.ok(ApiResponse.success(show));
    }
    
    @GetMapping
    @Operation(summary = "Get all shows", description = "Retrieve list of all shows")
    public ResponseEntity<ApiResponse<List<Show>>> getAllShows() {
        logger.info("Fetching all shows");
        
        List<Show> shows = showService.getAllShows();
        return ResponseEntity.ok(ApiResponse.success(shows));
    }
}
