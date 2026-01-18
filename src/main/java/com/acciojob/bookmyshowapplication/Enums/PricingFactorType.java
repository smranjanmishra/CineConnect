package com.acciojob.bookmyshowapplication.Enums;

public enum PricingFactorType {
    DEMAND_BASED,      // Based on seat occupancy
    TIME_BASED,        // Based on show time (morning/afternoon/evening/night)
    DAY_BASED,         // Based on weekday vs weekend
    SEAT_TYPE_BASED    // Based on seat type (already handled by base price)
}
