package com.acciojob.bookmyshowapplication.Controllers;

import com.acciojob.bookmyshowapplication.Requests.GetAvailableSeatsRequest;
import com.acciojob.bookmyshowapplication.Requests.SeatSelectionRequest;
import com.acciojob.bookmyshowapplication.Service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("seats")
public class SeatController {
    @Autowired
    private SeatService seatService;

    @PostMapping("/getAvailableSeats")
    public ResponseEntity<?> getAvailableSeats(@RequestBody GetAvailableSeatsRequest request) {
        try {
            Map<String, Object> seatLayout = seatService.getAvailableSeatsWithLayout(request);
            return ResponseEntity.ok(seatLayout);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching seats: " + e.getMessage());
        }
    }

    @PostMapping("/selectSeats")
    public ResponseEntity<?> selectSeats(@RequestBody SeatSelectionRequest request) {
        try {
            Map<String, Object> selectionResult = seatService.selectSeats(request);
            return ResponseEntity.ok(selectionResult);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error selecting seats: " + e.getMessage());
        }
    }

    @PostMapping("/releaseSeats")
    public ResponseEntity<?> releaseTemporarySeats(@RequestBody SeatSelectionRequest request) {
        try {
            String result = seatService.releaseTemporarySeats(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error releasing seats: " + e.getMessage());
        }
    }
}
