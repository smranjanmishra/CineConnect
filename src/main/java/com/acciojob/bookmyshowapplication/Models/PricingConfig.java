package com.acciojob.bookmyshowapplication.Models;

import com.acciojob.bookmyshowapplication.Enums.PricingFactorType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pricing_configs")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PricingConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer configId;

    @Enumerated(EnumType.STRING)
    private PricingFactorType factorType;

    private String configKey;        // e.g., "HIGH_DEMAND", "MORNING_SHOW", "WEEKEND"
    private Double multiplier;       // e.g., 1.5 for 50% increase, 0.8 for 20% discount
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // For demand-based pricing thresholds
    private Integer minOccupancyPercent;
    private Integer maxOccupancyPercent;

    // For time-based pricing ranges
    private Integer startHour;
    private Integer endHour;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
