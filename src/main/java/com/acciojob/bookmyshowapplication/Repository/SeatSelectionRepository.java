package com.acciojob.bookmyshowapplication.Repository;

import com.acciojob.bookmyshowapplication.Models.SeatSelection;
import com.acciojob.bookmyshowapplication.Models.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

public interface SeatSelectionRepository extends JpaRepository<SeatSelection, Integer> {
    List<SeatSelection> findByShowAndStatusAndCreatedAtAfter(Show show, String status, Date cutoffTime);

    @Modifying
    @Transactional
    void deleteByUserMobNoAndShow(String userMobNo, Show show);

    @Modifying
    @Transactional
    @Query("DELETE FROM SeatSelection s WHERE s.status = 'TEMP' AND s.createdAt < :cutoffTime")
    void deleteExpiredTempSelections(Date cutoffTime);
}
