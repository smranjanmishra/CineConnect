package com.acciojob.bookmyshowapplication.Service;

import com.acciojob.bookmyshowapplication.Enums.SeatType;
import com.acciojob.bookmyshowapplication.Exceptions.ResourceNotFoundException;
import com.acciojob.bookmyshowapplication.Models.*;
import com.acciojob.bookmyshowapplication.Repository.MovieRepository;
import com.acciojob.bookmyshowapplication.Repository.ShowRepository;
import com.acciojob.bookmyshowapplication.Repository.ShowSeatRepository;
import com.acciojob.bookmyshowapplication.Repository.TheaterRepository;
import com.acciojob.bookmyshowapplication.Requests.AddShowRequest;
import com.acciojob.bookmyshowapplication.Requests.AddShowSeatsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for show management
 */
@Service
public class ShowService {
    
    private static final Logger logger = LoggerFactory.getLogger(ShowService.class);
    
    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;

    /**
     * Schedule a new movie show
     */
    public String addShows(AddShowRequest showRequest) {
        logger.info("Creating show for movie: {} at theater: {}", 
                showRequest.getMovieName(), showRequest.getTheaterId());

        //Build an object of the Show Entity and save it to the DB
        Movie movie = movieRepository.findMovie(showRequest.getMovieName());
        if (movie == null) {
            throw new ResourceNotFoundException("Movie", "movieName", showRequest.getMovieName());
        }
        
        Theater theater = theaterRepository.findById(showRequest.getTheaterId())
                .orElseThrow(() -> new ResourceNotFoundException("Theater", "theaterId", showRequest.getTheaterId()));
        
        Show show = Show.builder()
                .showDate(showRequest.getShowDate())
                .showTime(showRequest.getShowTime())
                .movie(movie)
                .theater(theater)
                .build();
        show = showRepository.save(show);
        
        logger.info("Show created successfully with ID: {}", show.getShowId());
        return "Show has been saved to the database with showId: " + show.getShowId();
    }

    /**
     * Initialize seats for a show
     */
    public String addShowSeats(AddShowSeatsRequest showSeatsRequest) {
        logger.info("Adding seats to show ID: {}", showSeatsRequest.getShowId());
        
        Integer showId = showSeatsRequest.getShowId();
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show", "showId", showId));
        Theater theater = show.getTheater();
        List<TheaterSeat> theaterSeatList = theater.getTheaterSeatList();

        //Your goal is generation of show Seat List
        List<ShowSeat> showSeatList = new ArrayList<>();

        for(TheaterSeat theaterSeat:theaterSeatList){
            ShowSeat showSeat = ShowSeat.builder()
                    .seatNo(theaterSeat.getSeatNo())
                    .seatType(theaterSeat.getSeatType())
                    .isAvailable(Boolean.TRUE)
                    .show(show)
                    .build();

            if(theaterSeat.getSeatType().equals(SeatType.CLASSIC)){
                showSeat.setPrice(showSeatsRequest.getPriceOfClassicSeats());
            }
            else
                showSeat.setPrice(showSeatsRequest.getPriceOfPremiumSeats());

            showSeatList.add(showSeat);
        }

        showSeatRepository.saveAll(showSeatList);
        logger.info("Generated {} seats for show ID: {}", showSeatList.size(), showId);
        
        return "Show seats have been generated successfully for showId: " + showId;
    }
    
    /**
     * Get all shows
     */
    public List<Show> getAllShows() {
        logger.info("Fetching all shows");
        return showRepository.findAll();
    }
    
    /**
     * Get show by ID
     */
    public Show getShowById(Integer showId) {
        logger.info("Fetching show with ID: {}", showId);
        
        return showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show", "showId", showId));
    }
}