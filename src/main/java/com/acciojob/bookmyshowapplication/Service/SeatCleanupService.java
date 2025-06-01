package com.acciojob.bookmyshowapplication.Service;

import com.acciojob.bookmyshowapplication.Repository.SeatSelectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class SeatCleanupService {
    @Autowired
    private SeatSelectionRepository seatSelectionRepository;

    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void cleanupExpiredSelections() {
        Date cutoffTime = new Date(System.currentTimeMillis() - 10 * 60 * 1000); // 10 minutes ago
        seatSelectionRepository.deleteExpiredTempSelections(cutoffTime);
    }
}
