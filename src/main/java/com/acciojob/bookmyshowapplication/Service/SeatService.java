package com.acciojob.bookmyshowapplication.Service;

import com.acciojob.bookmyshowapplication.Models.*;
import com.acciojob.bookmyshowapplication.Repository.*;
import com.acciojob.bookmyshowapplication.Requests.GetAvailableSeatsRequest;
import com.acciojob.bookmyshowapplication.Requests.SeatSelectionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeatService {

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private SeatSelectionRepository seatSelectionRepository;

    public Map<String, Object> getAvailableSeatsWithLayout(GetAvailableSeatsRequest request) {
        // Find the show
        Movie movie = movieRepository.findMovieByMovieName(request.getMovieName());
        Theater theater = theaterRepository.findById(request.getTheaterId()).get();
        Show show = showRepository.findShowByShowDateAndShowTimeAndMovieAndTheater(
                request.getShowDate(), request.getShowTime(), movie, theater);

        // Get all show seats
        List<ShowSeat> showSeats = showSeatRepository.findAllByShow(show);

        // Get temporarily selected seats
        List<SeatSelection> tempSelections = seatSelectionRepository.findByShowAndStatusAndCreatedAtAfter(
                show, "TEMP", new Date(System.currentTimeMillis() - 10 * 60 * 1000)); // 10 minutes

        Set<String> tempSelectedSeats = tempSelections.stream()
                .map(SeatSelection::getSeatNo)
                .collect(Collectors.toSet());

        // Create seat layout
        Map<String, Object> seatLayout = new HashMap<>();
        List<Map<String, Object>> seats = new ArrayList<>();

        for (ShowSeat seat : showSeats) {
            Map<String, Object> seatInfo = new HashMap<>();
            seatInfo.put("seatNo", seat.getSeatNo());
            seatInfo.put("seatType", seat.getSeatType().toString());
            seatInfo.put("price", seat.getPrice());

            if (!seat.getIsAvailable()) {
                seatInfo.put("status", "BOOKED");
            } else if (tempSelectedSeats.contains(seat.getSeatNo())) {
                seatInfo.put("status", "TEMP_SELECTED");
            } else {
                seatInfo.put("status", "AVAILABLE");
            }

            seats.add(seatInfo);
        }

        seatLayout.put("seats", seats);
        seatLayout.put("showId", show.getShowId());
        seatLayout.put("movieName", movie.getMovieName());
        seatLayout.put("theaterName", theater.getName());

        return seatLayout;
    }

    public Map<String, Object> selectSeats(SeatSelectionRequest request) {
        Show show = showRepository.findById(request.getShowId()).get();
        List<ShowSeat> showSeats = showSeatRepository.findAllByShow(show);

        // Check if seats are available
        List<String> unavailableSeats = new ArrayList<>();
        int totalAmount = 0;

        for (String seatNo : request.getSelectedSeats()) {
            ShowSeat seat = showSeats.stream()
                    .filter(s -> s.getSeatNo().equals(seatNo))
                    .findFirst()
                    .orElse(null);

            if (seat == null || !seat.getIsAvailable()) {
                unavailableSeats.add(seatNo);
            } else {
                totalAmount += seat.getPrice();
            }
        }

        if (!unavailableSeats.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Some seats are not available");
            response.put("unavailableSeats", unavailableSeats);
            return response;
        }

        // Clear previous temp selections for this user
        seatSelectionRepository.deleteByUserMobNoAndShow(request.getUserMobNo(), show);

        // Create temporary selections
        for (String seatNo : request.getSelectedSeats()) {
            SeatSelection selection = SeatSelection.builder()
                    .show(show)
                    .seatNo(seatNo)
                    .userMobNo(request.getUserMobNo())
                    .status("TEMP")
                    .createdAt(new Date())
                    .build();
            seatSelectionRepository.save(selection);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("totalAmount", totalAmount);
        response.put("selectedSeats", request.getSelectedSeats());
        response.put("message", "Seats temporarily selected for 10 minutes");

        return response;
    }

    public String releaseTemporarySeats(SeatSelectionRequest request) {
        Show show = showRepository.findById(request.getShowId()).get();
        seatSelectionRepository.deleteByUserMobNoAndShow(request.getUserMobNo(), show);
        return "Temporary seat selections released";
    }
}
