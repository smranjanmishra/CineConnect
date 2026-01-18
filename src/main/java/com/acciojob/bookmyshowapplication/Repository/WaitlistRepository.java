package com.acciojob.bookmyshowapplication.Repository;

import com.acciojob.bookmyshowapplication.Enums.WaitlistStatus;
import com.acciojob.bookmyshowapplication.Models.Show;
import com.acciojob.bookmyshowapplication.Models.Waitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WaitlistRepository extends JpaRepository<Waitlist, Integer> {

    List<Waitlist> findByShowAndStatusOrderByCreatedAtAsc(Show show, WaitlistStatus status);

    List<Waitlist> findByShowAndStatus(Show show, WaitlistStatus status);

    @Query("SELECT w FROM Waitlist w WHERE w.status = :status AND w.expiresAt < :now")
    List<Waitlist> findExpiredWaitlists(@Param("status") WaitlistStatus status, @Param("now") LocalDateTime now);

    List<Waitlist> findByUserUserIdAndStatus(Integer userId, WaitlistStatus status);

    Long countByShowAndStatus(Show show, WaitlistStatus status);
}
