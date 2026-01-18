package com.acciojob.bookmyshowapplication.Responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PricingResponse {
    private Map<String, Integer> basePrices;        // Seat type -> base price
    private Map<String, Integer> dynamicPrices;     // Seat type -> final price
    private List<String> appliedFactors;            // List of pricing factors applied
    private Double totalMultiplier;
    private String priceJustification;
}
