package com.acciojob.bookmyshowapplication.Service;

import com.acciojob.bookmyshowapplication.Enums.PricingFactorType;
import com.acciojob.bookmyshowapplication.Enums.SeatType;
import com.acciojob.bookmyshowapplication.Exceptions.ResourceNotFoundException;
import com.acciojob.bookmyshowapplication.Models.PricingConfig;
import com.acciojob.bookmyshowapplication.Models.Show;
import com.acciojob.bookmyshowapplication.Models.ShowSeat;
import com.acciojob.bookmyshowapplication.Repository.PricingConfigRepository;
import com.acciojob.bookmyshowapplication.Repository.ShowSeatRepository;
import com.acciojob.bookmyshowapplication.Responses.PricingResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.*;

/**
 * Service for managing dynamic pricing
 */
@Service
public class DynamicPricingService {
    
    private static final Logger logger = LoggerFactory.getLogger(DynamicPricingService.class);

    @Autowired
    private PricingConfigRepository pricingConfigRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;

    /**
     * Initialize default pricing configurations
     */
    @PostConstruct
    public void initializeDefaultPricingRules() {
        // Check if pricing rules already exist
        if (pricingConfigRepository.count() > 0) {
            return;
        }

        List<PricingConfig> defaultConfigs = new ArrayList<>();

        // Demand-based pricing
        defaultConfigs.add(PricingConfig.builder()
                .factorType(PricingFactorType.DEMAND_BASED)
                .configKey("HIGH_DEMAND")
                .multiplier(1.5)
                .description("High demand - Less than 30% seats available")
                .isActive(true)
                .minOccupancyPercent(70)
                .maxOccupancyPercent(100)
                .build());

        defaultConfigs.add(PricingConfig.builder()
                .factorType(PricingFactorType.DEMAND_BASED)
                .configKey("MEDIUM_DEMAND")
                .multiplier(1.2)
                .description("Medium demand - 30-50% seats available")
                .isActive(true)
                .minOccupancyPercent(50)
                .maxOccupancyPercent(70)
                .build());

        defaultConfigs.add(PricingConfig.builder()
                .factorType(PricingFactorType.DEMAND_BASED)
                .configKey("NORMAL_DEMAND")
                .multiplier(1.0)
                .description("Normal demand - More than 50% seats available")
                .isActive(true)
                .minOccupancyPercent(0)
                .maxOccupancyPercent(50)
                .build());

        // Time-based pricing
        defaultConfigs.add(PricingConfig.builder()
                .factorType(PricingFactorType.TIME_BASED)
                .configKey("MORNING_SHOW")
                .multiplier(0.8)
                .description("Morning show discount (before 12 PM)")
                .isActive(true)
                .startHour(6)
                .endHour(12)
                .build());

        defaultConfigs.add(PricingConfig.builder()
                .factorType(PricingFactorType.TIME_BASED)
                .configKey("AFTERNOON_SHOW")
                .multiplier(0.9)
                .description("Afternoon show (12 PM - 6 PM)")
                .isActive(true)
                .startHour(12)
                .endHour(18)
                .build());

        defaultConfigs.add(PricingConfig.builder()
                .factorType(PricingFactorType.TIME_BASED)
                .configKey("EVENING_SHOW")
                .multiplier(1.3)
                .description("Evening show premium (6 PM - 10 PM)")
                .isActive(true)
                .startHour(18)
                .endHour(22)
                .build());

        defaultConfigs.add(PricingConfig.builder()
                .factorType(PricingFactorType.TIME_BASED)
                .configKey("NIGHT_SHOW")
                .multiplier(1.1)
                .description("Night show (after 10 PM)")
                .isActive(true)
                .startHour(22)
                .endHour(24)
                .build());

        // Day-based pricing
        defaultConfigs.add(PricingConfig.builder()
                .factorType(PricingFactorType.DAY_BASED)
                .configKey("WEEKEND")
                .multiplier(1.25)
                .description("Weekend pricing premium")
                .isActive(true)
                .build());

        defaultConfigs.add(PricingConfig.builder()
                .factorType(PricingFactorType.DAY_BASED)
                .configKey("WEEKDAY")
                .multiplier(1.0)
                .description("Regular weekday pricing")
                .isActive(true)
                .build());

        pricingConfigRepository.saveAll(defaultConfigs);
    }

    /**
     * Calculate dynamic price for a show
     */
    public PricingResponse calculateDynamicPricing(Show show) {
        List<ShowSeat> allSeats = showSeatRepository.findAllByShow(show);
        
        // Calculate occupancy
        long totalSeats = allSeats.size();
        long bookedSeats = allSeats.stream().filter(seat -> !seat.getIsAvailable()).count();
        int occupancyPercent = (int) ((bookedSeats * 100) / totalSeats);

        // Collect applied pricing factors
        List<String> appliedFactors = new ArrayList<>();
        double totalMultiplier = 1.0;

        // 1. Demand-based pricing
        Optional<PricingConfig> demandConfig = pricingConfigRepository
                .findDemandBasedPricing(PricingFactorType.DEMAND_BASED, occupancyPercent);
        if (demandConfig.isPresent()) {
            totalMultiplier *= demandConfig.get().getMultiplier();
            appliedFactors.add(demandConfig.get().getDescription());
        }

        // 2. Time-based pricing
        int showHour = show.getShowTime().getHour();
        Optional<PricingConfig> timeConfig = pricingConfigRepository
                .findTimeBasedPricing(PricingFactorType.TIME_BASED, showHour);
        if (timeConfig.isPresent()) {
            totalMultiplier *= timeConfig.get().getMultiplier();
            appliedFactors.add(timeConfig.get().getDescription());
        }

        // 3. Day-based pricing
        DayOfWeek dayOfWeek = show.getShowDate().getDayOfWeek();
        boolean isWeekend = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
        Optional<PricingConfig> dayConfig = pricingConfigRepository
                .findByConfigKeyAndIsActiveTrue(isWeekend ? "WEEKEND" : "WEEKDAY");
        if (dayConfig.isPresent()) {
            totalMultiplier *= dayConfig.get().getMultiplier();
            appliedFactors.add(dayConfig.get().getDescription());
        }

        // Calculate base and dynamic prices by seat type
        Map<String, Integer> basePrices = new HashMap<>();
        Map<String, Integer> dynamicPrices = new HashMap<>();

        // Get base prices from existing seats
        for (ShowSeat seat : allSeats) {
            String seatTypeKey = seat.getSeatType().toString();
            if (!basePrices.containsKey(seatTypeKey)) {
                basePrices.put(seatTypeKey, seat.getPrice());
                dynamicPrices.put(seatTypeKey, (int) (seat.getPrice() * totalMultiplier));
            }
        }

        // Build justification message
        String justification = String.format(
                "Pricing calculated based on: %d%% occupancy, %s time slot, %s",
                occupancyPercent,
                getTimeSlotName(showHour),
                isWeekend ? "Weekend" : "Weekday"
        );

        return PricingResponse.builder()
                .basePrices(basePrices)
                .dynamicPrices(dynamicPrices)
                .appliedFactors(appliedFactors)
                .totalMultiplier(totalMultiplier)
                .priceJustification(justification)
                .build();
    }

    /**
     * Apply dynamic pricing to show seats
     */
    public void applyDynamicPricingToShow(Show show) {
        PricingResponse pricingResponse = calculateDynamicPricing(show);
        List<ShowSeat> showSeats = showSeatRepository.findAllByShow(show);

        for (ShowSeat seat : showSeats) {
            String seatTypeKey = seat.getSeatType().toString();
            Integer dynamicPrice = pricingResponse.getDynamicPrices().get(seatTypeKey);
            if (dynamicPrice != null) {
                seat.setPrice(dynamicPrice);
            }
        }

        showSeatRepository.saveAll(showSeats);
    }

    /**
     * Calculate price for specific seat considering dynamic factors
     */
    public Integer calculateSeatPrice(Show show, SeatType seatType, Integer basePrice) {
        PricingResponse pricingResponse = calculateDynamicPricing(show);
        return (int) (basePrice * pricingResponse.getTotalMultiplier());
    }

    /**
     * Get pricing configuration
     */
    public List<PricingConfig> getAllPricingConfigs() {
        return pricingConfigRepository.findAll();
    }

    /**
     * Get active pricing configurations
     */
    public List<PricingConfig> getActivePricingConfigs() {
        return pricingConfigRepository.findByIsActiveTrue();
    }

    /**
     * Update pricing configuration
     */
    public PricingConfig updatePricingConfig(Integer configId, PricingConfig updatedConfig) {
        logger.info("Updating pricing config: {}", configId);
        
        PricingConfig existing = pricingConfigRepository.findById(configId)
                .orElseThrow(() -> new ResourceNotFoundException("PricingConfig", "configId", configId));

        existing.setMultiplier(updatedConfig.getMultiplier());
        existing.setDescription(updatedConfig.getDescription());
        existing.setIsActive(updatedConfig.getIsActive());
        existing.setMinOccupancyPercent(updatedConfig.getMinOccupancyPercent());
        existing.setMaxOccupancyPercent(updatedConfig.getMaxOccupancyPercent());
        existing.setStartHour(updatedConfig.getStartHour());
        existing.setEndHour(updatedConfig.getEndHour());

        PricingConfig saved = pricingConfigRepository.save(existing);
        logger.info("Pricing config {} updated successfully", configId);
        
        return saved;
    }

    private String getTimeSlotName(int hour) {
        if (hour >= 6 && hour < 12) return "Morning";
        if (hour >= 12 && hour < 18) return "Afternoon";
        if (hour >= 18 && hour < 22) return "Evening";
        return "Night";
    }
}
