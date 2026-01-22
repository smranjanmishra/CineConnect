package com.acciojob.bookmyshowapplication.Service;

import com.acciojob.bookmyshowapplication.Enums.TicketStatus;
import com.acciojob.bookmyshowapplication.Exceptions.ResourceNotFoundException;
import com.acciojob.bookmyshowapplication.Exceptions.SeatUnavailableException;
import com.acciojob.bookmyshowapplication.Models.*;
import com.acciojob.bookmyshowapplication.Repository.*;
import com.acciojob.bookmyshowapplication.Requests.BookTicketRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for ticket booking and management
 */
@Service
public class TicketService {
    
    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    // NEW: Add SeatSelectionRepository
    @Autowired
    private SeatSelectionRepository seatSelectionRepository;

    // NEW: Add DynamicPricingService
    @Autowired
    private DynamicPricingService dynamicPricingService;

    /**
     * Book tickets for a movie show
     */
    public Ticket bookTicket(BookTicketRequest bookTicketRequest) {
        logger.info("Booking ticket for movie: {} by user: {}", 
                bookTicketRequest.getMovieName(), bookTicketRequest.getMobNo());

        // NEW: First, get the show and check temporary seat selections
        Movie movie = movieRepository.findMovieByMovieName(bookTicketRequest.getMovieName());
        if (movie == null) {
            throw new ResourceNotFoundException("Movie", "movieName", bookTicketRequest.getMovieName());
        }
        
        Theater theater = theaterRepository.findById(bookTicketRequest.getTheaterId())
                .orElseThrow(() -> new ResourceNotFoundException("Theater", "theaterId", bookTicketRequest.getTheaterId()));

        //1.1 Find the ShowEntity with this date and Time
        Show show = showRepository.findShowByShowDateAndShowTimeAndMovieAndTheater(
                bookTicketRequest.getShowDate(),
                bookTicketRequest.getShowTime(),
                movie,
                theater
        );
        
        if (show == null) {
            throw new ResourceNotFoundException("Show not found for the given date, time, movie and theater");
        }

        // NEW: Check if seats are temporarily selected by the user
        Date cutoffTime = new Date(System.currentTimeMillis() - 10 * 60 * 1000); // 10 minutes ago
        List<SeatSelection> userTempSelections = seatSelectionRepository.findByShowAndStatusAndCreatedAtAfter(
                show, "TEMP", cutoffTime
        );

        List<String> userSelectedSeats = userTempSelections.stream()
                .filter(s -> s.getUserMobNo().equals(bookTicketRequest.getMobNo()))
                .map(SeatSelection::getSeatNo)
                .collect(Collectors.toList());

        // NEW: Verify requested seats match temp selections (Optional - you can remove this check if you want to allow direct booking)
        if (!userSelectedSeats.containsAll(bookTicketRequest.getRequestedSeats())) {
            throw new SeatUnavailableException("Please select seats first through the seat selection interface. Some requested seats are not in your temporary selection.");
        }

        // EXISTING CODE: Calculate the total cost of the tickets
        Integer showId = show.getShowId();
        List<ShowSeat> showSeatList = showSeatRepository.findShowSeats(showId);

        //Calculate the total Amt and if all seats mentioned are available or not
        int totalAmount = 0;
        Boolean areAllSeatsAvailable = Boolean.TRUE;

        for(String seatNo:bookTicketRequest.getRequestedSeats()) {
            for(ShowSeat showSeat:showSeatList) {
                if(showSeat.getSeatNo().equals(seatNo))
                {
                    if(showSeat.getIsAvailable()==Boolean.FALSE){
                        areAllSeatsAvailable = Boolean.FALSE;
                        break;
                    }
                    totalAmount = totalAmount+showSeat.getPrice();
                }
            }
        }

        if(areAllSeatsAvailable==Boolean.FALSE){
            throw new SeatUnavailableException("The requested Seats are unavailable");
        }

        // EXISTING CODE: Make the seats booked:(Only if seats are available : otherwise throw exception)
        for(String seatNo:bookTicketRequest.getRequestedSeats()) {
            for(ShowSeat showSeat:showSeatList) {
                if(showSeat.getSeatNo().equals(seatNo))
                {
                    showSeat.setIsAvailable(Boolean.FALSE);
                }
            }
        }

        // EXISTING CODE: Save updated show seats
        showSeatRepository.saveAll(showSeatList);

        User user = userRepository.findUserByMobNo(bookTicketRequest.getMobNo());
        if (user == null) {
            throw new ResourceNotFoundException("User", "mobile number", bookTicketRequest.getMobNo());
        }

        // EXISTING CODE: Save the ticketEntity
        Ticket ticket = Ticket.builder()
                .user(user)
                .movieName(bookTicketRequest.getMovieName())
                .showDate(bookTicketRequest.getShowDate())
                .theaterNameAndAddress(theater.getName()+" "+theater.getAddress())
                .showTime(bookTicketRequest.getShowTime())
                .totalAmtPaid(totalAmount)
                .show(show)
                .ticketStatus(TicketStatus.CONFIRMED)
                .build();

        ticket = ticketRepository.save(ticket);
        logger.info("Ticket booked successfully with ID: {}", ticket.getTicketId());

        // NEW: Apply dynamic pricing for future bookings (optional - can be done periodically)
        // This ensures prices are updated based on current demand
        try {
            dynamicPricingService.applyDynamicPricingToShow(show);
        } catch (Exception e) {
            // Log error but don't fail the booking
            logger.error("Error applying dynamic pricing: {}", e.getMessage());
        }

        // NEW: After successful booking, update temp selections to CONFIRMED and clean up
        for (SeatSelection selection : userTempSelections) {
            if (bookTicketRequest.getRequestedSeats().contains(selection.getSeatNo())
                    && selection.getUserMobNo().equals(bookTicketRequest.getMobNo())) {
                selection.setStatus("CONFIRMED");
                seatSelectionRepository.save(selection);
            }
        }

        // NEW: Clean up any other temporary selections for these seats from other users
        List<SeatSelection> allTempSelections = seatSelectionRepository.findByShowAndStatusAndCreatedAtAfter(
                show, "TEMP", cutoffTime
        );

        for (SeatSelection selection : allTempSelections) {
            if (bookTicketRequest.getRequestedSeats().contains(selection.getSeatNo())
                    && !selection.getUserMobNo().equals(bookTicketRequest.getMobNo())) {
                seatSelectionRepository.delete(selection);
            }
        }

        return ticket;
    }
    
    /**
     * Get ticket by ID
     */
    public Ticket getTicketById(String ticketId) {
        logger.info("Fetching ticket with ID: {}", ticketId);
        
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "ticketId", ticketId));
    }
    
    /**
     * Get all tickets for a user
     */
    public List<Ticket> getUserTickets(Integer userId) {
        logger.info("Fetching tickets for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        
        return ticketRepository.findByUser(user);
    }
}