package com.acciojob.bookmyshowapplication.Controllers;

import com.acciojob.bookmyshowapplication.Models.Movie;
import com.acciojob.bookmyshowapplication.Requests.UpdateMovieRequest;
import com.acciojob.bookmyshowapplication.Responses.ApiResponse;
import com.acciojob.bookmyshowapplication.Service.MovieService;
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
 * REST Controller for movie management operations
 */
@RestController
@RequestMapping("/api/v1/movies")
@Tag(name = "Movie Management", description = "APIs for managing movies")
public class MovieController {
    
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);
    
    @Autowired
    private MovieService movieService;

    @PostMapping
    @Operation(summary = "Add movie", description = "Add a new movie to the catalog")
    public ResponseEntity<ApiResponse<String>> addMovie(@Valid @RequestBody Movie movie) {
        logger.info("Adding new movie: {}", movie.getMovieName());
        
        String response = movieService.addMovie(movie);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Movie added successfully", response));
    }

    @PutMapping("/{movieId}")
    @Operation(summary = "Update movie", description = "Update movie attributes")
    public ResponseEntity<ApiResponse<String>> updateMovie(
            @PathVariable Integer movieId,
            @Valid @RequestBody UpdateMovieRequest movieRequest) {
        
        logger.info("Updating movie with ID: {}", movieId);
        
        String result = movieService.updateMovieAttributes(movieRequest);
        return ResponseEntity.ok(ApiResponse.success("Movie updated successfully", result));
    }
    
    @GetMapping
    @Operation(summary = "Get all movies", description = "Retrieve list of all movies")
    public ResponseEntity<ApiResponse<List<Movie>>> getAllMovies() {
        logger.info("Fetching all movies");
        
        List<Movie> movies = movieService.getAllMovies();
        return ResponseEntity.ok(ApiResponse.success(movies));
    }
    
    @GetMapping("/{movieId}")
    @Operation(summary = "Get movie by ID", description = "Retrieve movie details by ID")
    public ResponseEntity<ApiResponse<Movie>> getMovieById(@PathVariable Integer movieId) {
        logger.info("Fetching movie with ID: {}", movieId);
        
        Movie movie = movieService.getMovieById(movieId);
        return ResponseEntity.ok(ApiResponse.success(movie));
    }
}
