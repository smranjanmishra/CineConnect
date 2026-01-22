package com.acciojob.bookmyshowapplication.Repository;

import com.acciojob.bookmyshowapplication.Models.Ticket;
import com.acciojob.bookmyshowapplication.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for Ticket entity
 */
public interface TicketRepository extends JpaRepository<Ticket, String> {
    
    /**
     * Find all tickets for a specific user
     */
    List<Ticket> findByUser(User user);
}
