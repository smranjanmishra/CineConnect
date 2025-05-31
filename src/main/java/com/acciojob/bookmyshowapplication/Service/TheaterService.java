package com.acciojob.bookmyshowapplication.Service;

import com.acciojob.bookmyshowapplication.Enums.SeatType;
import com.acciojob.bookmyshowapplication.Models.Theater;
import com.acciojob.bookmyshowapplication.Models.TheaterSeat;
import com.acciojob.bookmyshowapplication.Repository.TheaterRepository;
import com.acciojob.bookmyshowapplication.Repository.TheaterSeatRepository;
import com.acciojob.bookmyshowapplication.Requests.AddTheaterRequest;
import com.acciojob.bookmyshowapplication.Requests.AddTheaterSeatsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class TheaterService {

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private TheaterSeatRepository theaterSeatRepository;

    public String addTheater(AddTheaterRequest addTheaterRequest) {
        // Convert this addRequest to an Entity

        Theater theater = Theater.builder()
                .address(addTheaterRequest.getAddress())
                .noOfScreens(addTheaterRequest.getNoOfScreens())
                .name(addTheaterRequest.getName())
                .build();

        //Save the entity to the DB
        theater = theaterRepository.save(theater);
        return "The theater is with a theaterId "+theater.getTheaterId();
    }

    public String addTheaterSeats(AddTheaterSeatsRequest addTheaterSeatsRequest) {
        int noOfClassicSeats = addTheaterSeatsRequest.getNoOfClassicSeats();
        int noOfPremiumSeats = addTheaterSeatsRequest.getNoOfPremiumSeats();
        Integer theaterId = addTheaterSeatsRequest.getTheaterId();
        Theater theater = theaterRepository.findById(theaterId).get();

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

        //Theater seats will get automatically saved
        //bcz of cascading property written in the parent table
        return "Theater seats have been generated";
    }
}