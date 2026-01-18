package com.acciojob.bookmyshowapplication.Models;

import com.acciojob.bookmyshowapplication.Enums.RefundStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "refund_transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefundTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer refundId;

    @OneToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    private Integer originalAmount;
    private Integer refundAmount;
    private Double refundPercentage;
    private Integer cancellationCharges;

    @Enumerated(EnumType.STRING)
    private RefundStatus refundStatus;

    private String refundMethod;  // e.g., "WALLET", "BANK", "ORIGINAL_PAYMENT_METHOD"
    private String transactionId;
    private String failureReason;

    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
    private LocalDateTime failedAt;

    @PrePersist
    protected void onCreate() {
        initiatedAt = LocalDateTime.now();
    }
}
