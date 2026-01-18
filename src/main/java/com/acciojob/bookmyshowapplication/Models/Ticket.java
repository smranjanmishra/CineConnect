package com.acciojob.bookmyshowapplication.Models;

import com.acciojob.bookmyshowapplication.Enums.RefundStatus;
import com.acciojob.bookmyshowapplication.Enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//import org.apache.catalina.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "tickets")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String ticketId;

    private String movieName;
    private LocalDate showDate;
    private LocalTime showTime;
    private String theaterNameAndAddress;
    private Integer totalAmtPaid;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TicketStatus ticketStatus = TicketStatus.CONFIRMED;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RefundStatus refundStatus = RefundStatus.NOT_APPLICABLE;

    private LocalDateTime bookedAt;
    private LocalDateTime cancelledAt;
    private Integer refundAmount;
    private String cancellationReason;
    private Double refundPercentage;

    @ManyToOne
    @JoinColumn
    private User user;

    @ManyToOne
    @JoinColumn
    private Show show;

    @PrePersist
    protected void onCreate() {
        bookedAt = LocalDateTime.now();
    }
}
