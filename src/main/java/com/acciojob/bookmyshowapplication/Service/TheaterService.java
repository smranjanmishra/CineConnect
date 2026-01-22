package com.acciojob.bookmyshowapplication.Service;

import com.acciojob.bookmyshowapplication.Enums.SeatType;
import com.acciojob.bookmyshowapplication.Exceptions.ResourceNotFoundException;
import com.acciojob.bookmyshowapplication.Models.Theater;
import com.acciojob.bookmyshowapplication.Models.TheaterSeat;
import com.acciojob.bookmyshowapplication.Repository.TheaterRepository;
import com.acciojob.bookmyshowapplication.Repository.TheaterSeatRepository;
import com.acciojob.bookmyshowapplication.Requests.AddTheaterRequest;
import com.acciojob.bookmyshowapplication.Requests.AddTheaterSeatsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for theater management
 */
@Service
public class TheaterService {
    
    private static final Logger logger = LoggerFactory.getLogger(TheaterService.class);

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private TheaterSeatRepository theaterSeatRepository;

    /**
     * Add a new theater to the system
     */
    public String addTheater(AddTheaterRequest addTheaterRequest) {
        logger.info("Adding new theater: {}", addTheaterRequest.getName());
        // Convert this addRequest to an Entity

        Theater theater = Theater.builder()
                .address(addTheaterRequest.getAddress())
                .noOfScreens(addTheaterRequest.getNoOfScreens())
                .name(addTheaterRequest.getName())
                .build();

        //Save the entity to the DB
        theater = theaterRepository.save(theater);
        logger.info("Theater created successfully with ID: {}", theater.getTheaterId());
        
        return "Theater has been saved with theaterId: " + theater.getTheaterId();
    }

    /**
     * Add seats to a theater
     */
    public String addTheaterSeats(AddTheaterSeatsRequest addTheaterSeatsRequest) {
        logger.info("Adding seats to theater ID: {}", addTheaterSeatsRequest.getTheaterId());
        int noOfClassicSeats = addTheaterSeatsRequest.getNoOfClassicSeats();
        int noOfPremiumSeats = addTheaterSeatsRequest.getNoOfPremiumSeats();
        Integer theaterId = addTheaterSeatsRequest.getTheaterId();
        
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new ResourceNotFoundException("Theater", "theaterId", theaterId));

        int classicSeatCounter = 1;
        char ch = 'A';
        int rowNo = 1;

        List<TheaterSeat> theaterSeatList = new ArrayList<>();

        // Generate Classic Seats
        while(classicSeatCounter <= noOfClassicSeats) {
            String seatNo = rowNo + "" + ch;
            TheaterSeat theaterSeat = TheaterSeat.builder()
                    .seatNo(seatNo)
                    .seatType(SeatType.CLASSIC)
                    .theater(theater)
                    .build();

            theaterSeatList.add(theaterSeat);
            ch++;

            if(classicSeatCounter % 5 == 0) {
                rowNo = rowNo + 1;
                ch = 'A';
            }
            classicSeatCounter++;
        }

        // Generate Premium Seats
        int premiumSeatCounter = 1;
        ch = 'A';

        if(classicSeatCounter % 5 != 1) {
            rowNo = rowNo + 1;
        }

        // FIXED: Changed condition from classicSeatCounter to premiumSeatCounter
        while(premiumSeatCounter <= noOfPremiumSeats) {
            String seatNo = rowNo + ch + "";
            TheaterSeat theaterSeat = TheaterSeat.builder()
                    .seatNo(seatNo)
                    .theater(theater)
                    .seatType(SeatType.PREMIUM)
                    .build();

            theaterSeatList.add(theaterSeat);
            ch++;

            if(premiumSeatCounter % 5 == 0) {
                rowNo = rowNo + 1;
                ch = 'A';
            }
            premiumSeatCounter++;
        }

        theater.setTheaterSeatList(theaterSeatList);
        theaterRepository.save(theater);
        
        logger.info("Added {} classic and {} premium seats to theater ID: {}", 
                noOfClassicSeats, noOfPremiumSeats, theaterId);

        //Theater seats will get automatically saved
        //because of cascading property written in the parent table
        return "Theater seats have been generated successfully";
    }
    
    /**
     * Get all theaters
     */
    public List<Theater> getAllTheaters() {
        logger.info("Fetching all theaters");
        return theaterRepository.findAll();
    }
    
    /**
     * Get theater by ID
     */
    public Theater getTheaterById(Integer theaterId) {
        logger.info("Fetching theater with ID: {}", theaterId);
        
        return theaterRepository.findById(theaterId)
                .orElseThrow(() -> new ResourceNotFoundException("Theater", "theaterId", theaterId));
    }
}