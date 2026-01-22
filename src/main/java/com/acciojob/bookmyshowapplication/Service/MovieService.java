package com.acciojob.bookmyshowapplication.Service;

import com.acciojob.bookmyshowapplication.Exceptions.ResourceNotFoundException;
import com.acciojob.bookmyshowapplication.Models.Movie;
import com.acciojob.bookmyshowapplication.Repository.MovieRepository;
import com.acciojob.bookmyshowapplication.Requests.UpdateMovieRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for movie management
 */
@Service
public class MovieService {
    
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    @Autowired
    private MovieRepository movieRepository;

    /**
     * Add a new movie to the catalog
     */
    public String addMovie(Movie movie) {
        logger.info("Adding new movie: {}", movie.getMovieName());
        
        movie = movieRepository.save(movie);
        logger.info("Movie saved successfully with ID: {}", movie.getMovieId());
        
        return "Movie has been saved to the database with movieId: " + movie.getMovieId();
    }

    /**
     * Update movie attributes (rating and duration)
     */
    public String updateMovieAttributes(UpdateMovieRequest movieRequest) {
        logger.info("Updating movie with ID: {}", movieRequest.getMovieId());
        
        Movie movie = movieRepository.findById(movieRequest.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "movieId", movieRequest.getMovieId()));

        movie.setDuration(movieRequest.getDuration());
        movie.setRating(movieRequest.getRating());

        movieRepository.save(movie);
        logger.info("Movie updated successfully: {}", movie.getMovieName());
        
        return "Movie attributes updated successfully";
    }
    
    /**
     * Get all movies
     */
    public List<Movie> getAllMovies() {
        logger.info("Fetching all movies");
        return movieRepository.findAll();
    }
    
    /**
     * Get movie by ID
     */
    public Movie getMovieById(Integer movieId) {
        logger.info("Fetching movie with ID: {}", movieId);
        
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "movieId", movieId));
    }
}