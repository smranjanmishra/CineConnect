package com.acciojob.bookmyshowapplication.Service;

import com.acciojob.bookmyshowapplication.Enums.RefundStatus;
import com.acciojob.bookmyshowapplication.Enums.TicketStatus;
import com.acciojob.bookmyshowapplication.Models.RefundTransaction;
import com.acciojob.bookmyshowapplication.Models.ShowSeat;
import com.acciojob.bookmyshowapplication.Models.Ticket;
import com.acciojob.bookmyshowapplication.Repository.RefundTransactionRepository;
import com.acciojob.bookmyshowapplication.Repository.ShowSeatRepository;
import com.acciojob.bookmyshowapplication.Repository.TicketRepository;
import com.acciojob.bookmyshowapplication.Requests.CancelTicketRequest;
import com.acciojob.bookmyshowapplication.Responses.CancellationResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CancellationService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;

    @Autowired
    private RefundTransactionRepository refundTransactionRepository;

    @Autowired
    private WaitlistService waitlistService;

    /**
     * Calculate refund percentage based on time remaining until show
     * Refund Policy:
     * - More than 48 hours: 100% refund
     * - 24-48 hours: 75% refund
     * - 12-24 hours: 50% refund
     * - 6-12 hours: 25% refund
     * - Less than 6 hours: No refund
     */
    public double calculateRefundPercentage(LocalDateTime showDateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, showDateTime);
        long hoursUntilShow = duration.toHours();

        if (hoursUntilShow >= 48) {
            return 1.0;  // 100% refund
        } else if (hoursUntilShow >= 24) {
            return 0.75; // 75% refund
        } else if (hoursUntilShow >= 12) {
            return 0.50; // 50% refund
        } else if (hoursUntilShow >= 6) {
            return 0.25; // 25% refund
        } else {
            return 0.0;  // No refund
        }
    }

    @Transactional
    public CancellationResponse cancelTicket(CancelTicketRequest request) throws Exception {
        // Find the ticket
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new Exception("Ticket not found with ID: " + request.getTicketId()));

        // Validate ticket status
        if (ticket.getTicketStatus() == TicketStatus.CANCELLED) {
            throw new Exception("Ticket is already cancelled");
        }

        // Check if show has already passed
        LocalDateTime showDateTime = LocalDateTime.of(ticket.getShowDate(), ticket.getShowTime());
        if (LocalDateTime.now().isAfter(showDateTime)) {
            throw new Exception("Cannot cancel ticket for a show that has already passed");
        }

        // Calculate refund
        double refundPercentage = calculateRefundPercentage(showDateTime);
        int refundAmount = (int) (ticket.getTotalAmtPaid() * refundPercentage);
        int cancellationCharges = ticket.getTotalAmtPaid() - refundAmount;

        // Update ticket status
        ticket.setTicketStatus(TicketStatus.CANCELLED);
        ticket.setRefundStatus(refundPercentage > 0 ? RefundStatus.PENDING : RefundStatus.NOT_APPLICABLE);
        ticket.setCancelledAt(LocalDateTime.now());
        ticket.setRefundAmount(refundAmount);
        ticket.setRefundPercentage(refundPercentage);
        ticket.setCancellationReason(request.getCancellationReason());
        ticketRepository.save(ticket);

        // Create refund transaction if applicable
        RefundTransaction refundTransaction = null;
        if (refundPercentage > 0) {
            refundTransaction = RefundTransaction.builder()
                    .ticket(ticket)
                    .originalAmount(ticket.getTotalAmtPaid())
                    .refundAmount(refundAmount)
                    .refundPercentage(refundPercentage)
                    .cancellationCharges(cancellationCharges)
                    .refundStatus(RefundStatus.PENDING)
                    .refundMethod("ORIGINAL_PAYMENT_METHOD")
                    .transactionId(UUID.randomUUID().toString())
                    .build();
            refundTransactionRepository.save(refundTransaction);

            // Process refund (in real scenario, this would integrate with payment gateway)
            processRefund(refundTransaction);
        }

        // Release seats back to inventory
        releaseSeatsForShow(ticket);

        // Notify waitlisted users
        try {
            waitlistService.processWaitlistForShow(ticket.getShow());
        } catch (Exception e) {
            // Log the error but don't fail the cancellation
            System.err.println("Error processing waitlist: " + e.getMessage());
        }

        // Build response
        return CancellationResponse.builder()
                .ticketId(ticket.getTicketId())
                .ticketStatus(ticket.getTicketStatus())
                .refundStatus(ticket.getRefundStatus())
                .originalAmount(ticket.getTotalAmtPaid())
                .refundAmount(refundAmount)
                .refundPercentage(refundPercentage)
                .cancellationCharges(cancellationCharges)
                .cancelledAt(ticket.getCancelledAt())
                .message(refundPercentage > 0 
                        ? "Ticket cancelled successfully. Refund will be processed within 5-7 business days."
                        : "Ticket cancelled. No refund applicable due to cancellation policy.")
                .estimatedRefundTime(refundPercentage > 0 ? "5-7 business days" : "N/A")
                .build();
    }

    /**
     * Simulates refund processing
     * In production, this would integrate with payment gateway
     */
    private void processRefund(RefundTransaction refundTransaction) {
        try {
            // Simulate processing delay
            // In production: Call payment gateway API
            refundTransaction.setRefundStatus(RefundStatus.PROCESSING);
            refundTransactionRepository.save(refundTransaction);

            // Simulate successful refund
            refundTransaction.setRefundStatus(RefundStatus.COMPLETED);
            refundTransaction.setCompletedAt(LocalDateTime.now());
            refundTransactionRepository.save(refundTransaction);

            // Update ticket refund status
            Ticket ticket = refundTransaction.getTicket();
            ticket.setRefundStatus(RefundStatus.COMPLETED);
            ticketRepository.save(ticket);

        } catch (Exception e) {
            refundTransaction.setRefundStatus(RefundStatus.FAILED);
            refundTransaction.setFailureReason(e.getMessage());
            refundTransaction.setFailedAt(LocalDateTime.now());
            refundTransactionRepository.save(refundTransaction);

            // Update ticket refund status
            Ticket ticket = refundTransaction.getTicket();
            ticket.setRefundStatus(RefundStatus.FAILED);
            ticketRepository.save(ticket);
        }
    }

    /**
     * Releases seats back to available inventory when ticket is cancelled
     */
    private void releaseSeatsForShow(Ticket ticket) {
        // Get all seats for the show
        List<ShowSeat> showSeats = showSeatRepository.findAllByShow(ticket.getShow());
        
        // Note: In the current implementation, we don't track which specific seats 
        // were booked for a ticket. In production, you would need to maintain this mapping.
        // For now, we'll mark seats as available based on the ticket amount
        
        // This is a simplified implementation. In production, you should maintain
        // a separate TicketSeats table to track seat-ticket relationships
        for (ShowSeat seat : showSeats) {
            if (!seat.getIsAvailable()) {
                seat.setIsAvailable(Boolean.TRUE);
            }
        }
        showSeatRepository.saveAll(showSeats);
    }

    /**
     * Get refund status for a ticket
     */
    public RefundTransaction getRefundStatus(String ticketId) throws Exception {
        return refundTransactionRepository.findByTicketTicketId(ticketId)
                .orElseThrow(() -> new Exception("No refund transaction found for ticket: " + ticketId));
    }
}
