package com.acciojob.bookmyshowapplication.Repository;

import com.acciojob.bookmyshowapplication.Enums.RefundStatus;
import com.acciojob.bookmyshowapplication.Models.RefundTransaction;
import com.acciojob.bookmyshowapplication.Models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefundTransactionRepository extends JpaRepository<RefundTransaction, Integer> {

    Optional<RefundTransaction> findByTicket(Ticket ticket);

    List<RefundTransaction> findByRefundStatus(RefundStatus refundStatus);

    Optional<RefundTransaction> findByTicketTicketId(String ticketId);
}
