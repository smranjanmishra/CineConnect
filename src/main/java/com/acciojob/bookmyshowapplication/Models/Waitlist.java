package com.acciojob.bookmyshowapplication.Models;

import com.acciojob.bookmyshowapplication.Enums.WaitlistStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "waitlists")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Waitlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer waitlistId;

    @ManyToOne
    @JoinColumn
    private User user;

    @ManyToOne
    @JoinColumn
    private Show show;

    private String requestedSeatType;  // Can be "CLASSIC" or "PREMIUM"
    private Integer numberOfSeats;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private WaitlistStatus status = WaitlistStatus.PENDING;

    private LocalDateTime createdAt;
    private LocalDateTime notifiedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime convertedAt;

    private String notificationMessage;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        // Waitlist expires after the show time
    }
}
