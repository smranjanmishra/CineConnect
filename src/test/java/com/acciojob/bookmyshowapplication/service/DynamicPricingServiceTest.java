package com.acciojob.bookmyshowapplication.service;

import com.acciojob.bookmyshowapplication.Enums.PricingFactorType;
import com.acciojob.bookmyshowapplication.Enums.SeatType;
import com.acciojob.bookmyshowapplication.Models.*;
import com.acciojob.bookmyshowapplication.Repository.PricingConfigRepository;
import com.acciojob.bookmyshowapplication.Repository.ShowSeatRepository;
import com.acciojob.bookmyshowapplication.Responses.PricingResponse;
import com.acciojob.bookmyshowapplication.Service.DynamicPricingService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DynamicPricingService using Mockito
 * Tests cover demand-based, time-based, and day-based pricing strategies
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Dynamic Pricing Service Tests")
class DynamicPricingServiceTest {

    @Mock
    private PricingConfigRepository pricingConfigRepository;

    @Mock
    private ShowSeatRepository showSeatRepository;

    @InjectMocks
    private DynamicPricingService dynamicPricingService;

    private Show testShow;
    private Movie testMovie;
    private Theater testTheater;
    private List<ShowSeat> showSeats;
    private List<PricingConfig> pricingConfigs;

    @BeforeEach
    void setUp() {
        // Setup test movie and theater
        testMovie = new Movie();
        testMovie.setMovieId(1);
        testMovie.setMovieName("Inception");

        testTheater = new Theater();
        testTheater.setTheaterId(1);
        testTheater.setName("PVR Cinemas");

        // Setup test show - Weekend evening show
        testShow = new Show();
        testShow.setShowId(1);
        testShow.setShowDate(LocalDate.now().with(DayOfWeek.SATURDAY));
        testShow.setShowTime(LocalTime.of(19, 0)); // 7 PM
        testShow.setMovie(testMovie);
        testShow.setTheater(testTheater);

        // Setup show seats
        showSeats = createShowSeats(100, 50); // 100 total, 50 booked

        // Setup pricing configs
        pricingConfigs = createDefaultPricingConfigs();
    }

    private List<ShowSeat> createShowSeats(int total, int booked) {
        List<ShowSeat> seats = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            ShowSeat seat = new ShowSeat();
            seat.setShowSeatId(i + 1);
            seat.setSeatNo("A" + (i + 1));
            seat.setSeatType(i < 50 ? SeatType.CLASSIC : SeatType.PREMIUM);
            seat.setPrice(i < 50 ? 200 : 400);
            seat.setIsAvailable(i >= booked);
            seat.setShow(testShow);
            seats.add(seat);
        }
        return seats;
    }

    private List<PricingConfig> createDefaultPricingConfigs() {
        List<PricingConfig> configs = new ArrayList<>();

        // Demand-based configs
        configs.add(createConfig(PricingFactorType.DEMAND_BASED, "HIGH_DEMAND", 1.5, 
            "High demand", 70, 100, null, null));
        configs.add(createConfig(PricingFactorType.DEMAND_BASED, "MEDIUM_DEMAND", 1.2, 
            "Medium demand", 50, 70, null, null));
        configs.add(createConfig(PricingFactorType.DEMAND_BASED, "NORMAL_DEMAND", 1.0, 
            "Normal demand", 0, 50, null, null));

        // Time-based configs
        configs.add(createConfig(PricingFactorType.TIME_BASED, "MORNING_SHOW", 0.8, 
            "Morning discount", null, null, 6, 12));
        configs.add(createConfig(PricingFactorType.TIME_BASED, "AFTERNOON_SHOW", 0.9, 
            "Afternoon pricing", null, null, 12, 18));
        configs.add(createConfig(PricingFactorType.TIME_BASED, "EVENING_SHOW", 1.3, 
            "Evening premium", null, null, 18, 22));
        configs.add(createConfig(PricingFactorType.TIME_BASED, "NIGHT_SHOW", 1.1, 
            "Night show", null, null, 22, 24));

        // Day-based configs
        configs.add(createConfig(PricingFactorType.DAY_BASED, "WEEKEND", 1.25, 
            "Weekend premium", null, null, null, null));
        configs.add(createConfig(PricingFactorType.DAY_BASED, "WEEKDAY", 1.0, 
            "Regular weekday", null, null, null, null));

        return configs;
    }

    private PricingConfig createConfig(PricingFactorType type, String key, double multiplier,
                                      String description, Integer minOccupancy, Integer maxOccupancy,
                                      Integer startHour, Integer endHour) {
        return PricingConfig.builder()
                .factorType(type)
                .configKey(key)
                .multiplier(multiplier)
                .description(description)
                .isActive(true)
                .minOccupancyPercent(minOccupancy)
                .maxOccupancyPercent(maxOccupancy)
                .startHour(startHour)
                .endHour(endHour)
                .build();
    }

    // ==================== Test 1: Demand-Based Pricing ====================

    @Test
    @Order(1)
    @DisplayName("Test 1: Calculate pricing with HIGH demand (>70% occupancy)")
    void test01_calculateDynamicPricing_HighDemand() {
        // Arrange - 85% occupancy
        List<ShowSeat> highDemandSeats = createShowSeats(100, 85);
        when(showSeatRepository.findAllByShow(testShow)).thenReturn(highDemandSeats);
        
        PricingConfig highDemand = pricingConfigs.stream()
            .filter(c -> c.getConfigKey().equals("HIGH_DEMAND"))
            .findFirst().get();
        when(pricingConfigRepository.findDemandBasedPricing(PricingFactorType.DEMAND_BASED, 85))
            .thenReturn(Optional.of(highDemand));
        
        when(pricingConfigRepository.findTimeBasedPricing(any(), anyInt()))
            .thenReturn(Optional.empty());
        when(pricingConfigRepository.findByConfigKeyAndIsActiveTrue(anyString()))
            .thenReturn(Optional.empty());

        // Act
        PricingResponse response = dynamicPricingService.calculateDynamicPricing(testShow);

        // Assert
        assertNotNull(response);
        assertTrue(response.getTotalMultiplier() >= 1.5, 
            "High demand should have at least 1.5x multiplier");
        assertTrue(response.getAppliedFactors().contains("High demand"));
        verify(showSeatRepository, times(1)).findAllByShow(testShow);
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Calculate pricing with MEDIUM demand (50-70% occupancy)")
    void test02_calculateDynamicPricing_MediumDemand() {
        // Arrange - 60% occupancy
        List<ShowSeat> mediumDemandSeats = createShowSeats(100, 60);
        when(showSeatRepository.findAllByShow(testShow)).thenReturn(mediumDemandSeats);
        
        PricingConfig mediumDemand = pricingConfigs.stream()
            .filter(c -> c.getConfigKey().equals("MEDIUM_DEMAND"))
            .findFirst().get();
        when(pricingConfigRepository.findDemandBasedPricing(PricingFactorType.DEMAND_BASED, 60))
            .thenReturn(Optional.of(mediumDemand));
        
        when(pricingConfigRepository.findTimeBasedPricing(any(), anyInt()))
            .thenReturn(Optional.empty());
        when(pricingConfigRepository.findByConfigKeyAndIsActiveTrue(anyString()))
            .thenReturn(Optional.empty());

        // Act
        PricingResponse response = dynamicPricingService.calculateDynamicPricing(testShow);

        // Assert
        assertTrue(response.getTotalMultiplier() >= 1.2);
        assertTrue(response.getAppliedFactors().contains("Medium demand"));
    }

    @Test
    @Order(3)
    @DisplayName("Test 3: Calculate pricing with NORMAL demand (<50% occupancy)")
    void test03_calculateDynamicPricing_NormalDemand() {
        // Arrange - 30% occupancy
        List<ShowSeat> normalDemandSeats = createShowSeats(100, 30);
        when(showSeatRepository.findAllByShow(testShow)).thenReturn(normalDemandSeats);
        
        PricingConfig normalDemand = pricingConfigs.stream()
            .filter(c -> c.getConfigKey().equals("NORMAL_DEMAND"))
            .findFirst().get();
        when(pricingConfigRepository.findDemandBasedPricing(PricingFactorType.DEMAND_BASED, 30))
            .thenReturn(Optional.of(normalDemand));
        
        when(pricingConfigRepository.findTimeBasedPricing(any(), anyInt()))
            .thenReturn(Optional.empty());
        when(pricingConfigRepository.findByConfigKeyAndIsActiveTrue(anyString()))
            .thenReturn(Optional.empty());

        // Act
        PricingResponse response = dynamicPricingService.calculateDynamicPricing(testShow);

        // Assert
        assertEquals(1.0, response.getTotalMultiplier(), 0.01);
        assertTrue(response.getAppliedFactors().contains("Normal demand"));
    }

    // ==================== Test 2: Time-Based Pricing ====================

    @Test
    @Order(4)
    @DisplayName("Test 4: Calculate pricing for MORNING show (6 AM - 12 PM)")
    void test04_calculateDynamicPricing_MorningShow() {
        // Arrange
        testShow.setShowTime(LocalTime.of(10, 0));
        when(showSeatRepository.findAllByShow(testShow)).thenReturn(showSeats);
        
        PricingConfig morningShow = pricingConfigs.stream()
            .filter(c -> c.getConfigKey().equals("MORNING_SHOW"))
            .findFirst().get();
        when(pricingConfigRepository.findTimeBasedPricing(PricingFactorType.TIME_BASED, 10))
            .thenReturn(Optional.of(morningShow));
        
        when(pricingConfigRepository.findDemandBasedPricing(any(), anyInt()))
            .thenReturn(Optional.empty());
        when(pricingConfigRepository.findByConfigKeyAndIsActiveTrue(anyString()))
            .thenReturn(Optional.empty());

        // Act
        PricingResponse response = dynamicPricingService.calculateDynamicPricing(testShow);

        // Assert
        assertTrue(response.getTotalMultiplier() <= 0.8, 
            "Morning show should have discount (0.8x multiplier)");
        assertTrue(response.getAppliedFactors().contains("Morning discount"));
    }

    @Test
    @Order(5)
    @DisplayName("Test 5: Calculate pricing for EVENING show (6 PM - 10 PM)")
    void test05_calculateDynamicPricing_EveningShow() {
        // Arrange
        testShow.setShowTime(LocalTime.of(19, 0));
        when(showSeatRepository.findAllByShow(testShow)).thenReturn(showSeats);
        
        PricingConfig eveningShow = pricingConfigs.stream()
            .filter(c -> c.getConfigKey().equals("EVENING_SHOW"))
            .findFirst().get();
        when(pricingConfigRepository.findTimeBasedPricing(PricingFactorType.TIME_BASED, 19))
            .thenReturn(Optional.of(eveningShow));
        
        when(pricingConfigRepository.findDemandBasedPricing(any(), anyInt()))
            .thenReturn(Optional.empty());
        when(pricingConfigRepository.findByConfigKeyAndIsActiveTrue(anyString()))
            .thenReturn(Optional.empty());

        // Act
        PricingResponse response = dynamicPricingService.calculateDynamicPricing(testShow);

        // Assert
        assertTrue(response.getTotalMultiplier() >= 1.3, 
            "Evening show should have premium (1.3x multiplier)");
        assertTrue(response.getAppliedFactors().contains("Evening premium"));
    }

    @Test
    @Order(6)
    @DisplayName("Test 6: Calculate pricing for AFTERNOON show (12 PM - 6 PM)")
    void test06_calculateDynamicPricing_AfternoonShow() {
        // Arrange
        testShow.setShowTime(LocalTime.of(14, 0));
        when(showSeatRepository.findAllByShow(testShow)).thenReturn(showSeats);
        
        PricingConfig afternoonShow = pricingConfigs.stream()
            .filter(c -> c.getConfigKey().equals("AFTERNOON_SHOW"))
            .findFirst().get();
        when(pricingConfigRepository.findTimeBasedPricing(PricingFactorType.TIME_BASED, 14))
            .thenReturn(Optional.of(afternoonShow));
        
        when(pricingConfigRepository.findDemandBasedPricing(any(), anyInt()))
            .thenReturn(Optional.empty());
        when(pricingConfigRepository.findByConfigKeyAndIsActiveTrue(anyString()))
            .thenReturn(Optional.empty());

        // Act
        PricingResponse response = dynamicPricingService.calculateDynamicPricing(testShow);

        // Assert
        assertTrue(response.getTotalMultiplier() >= 0.9);
        assertTrue(response.getAppliedFactors().contains("Afternoon pricing"));
    }

    // ==================== Test 3: Day-Based Pricing ====================

    @Test
    @Order(7)
    @DisplayName("Test 7: Calculate pricing for WEEKEND show")
    void test07_calculateDynamicPricing_WeekendShow() {
        // Arrange
        testShow.setShowDate(LocalDate.now().with(DayOfWeek.SATURDAY));
        when(showSeatRepository.findAllByShow(testShow)).thenReturn(showSeats);
        
        PricingConfig weekendConfig = pricingConfigs.stream()
            .filter(c -> c.getConfigKey().equals("WEEKEND"))
            .findFirst().get();
        when(pricingConfigRepository.findByConfigKeyAndIsActiveTrue("WEEKEND"))
            .thenReturn(Optional.of(weekendConfig));
        
        when(pricingConfigRepository.findDemandBasedPricing(any(), anyInt()))
            .thenReturn(Optional.empty());
        when(pricingConfigRepository.findTimeBasedPricing(any(), anyInt()))
            .thenReturn(Optional.empty());

        // Act
        PricingResponse response = dynamicPricingService.calculateDynamicPricing(testShow);

        // Assert
        assertTrue(response.getTotalMultiplier() >= 1.25, 
            "Weekend show should have premium (1.25x multiplier)");
        assertTrue(response.getAppliedFactors().contains("Weekend premium"));
    }

    @Test
    @Order(8)
    @DisplayName("Test 8: Calculate pricing for WEEKDAY show")
    void test08_calculateDynamicPricing_WeekdayShow() {
        // Arrange
        testShow.setShowDate(LocalDate.now().with(DayOfWeek.MONDAY));
        when(showSeatRepository.findAllByShow(testShow)).thenReturn(showSeats);
        
        PricingConfig weekdayConfig = pricingConfigs.stream()
            .filter(c -> c.getConfigKey().equals("WEEKDAY"))
            .findFirst().get();
        when(pricingConfigRepository.findByConfigKeyAndIsActiveTrue("WEEKDAY"))
            .thenReturn(Optional.of(weekdayConfig));
        
        when(pricingConfigRepository.findDemandBasedPricing(any(), anyInt()))
            .thenReturn(Optional.empty());
        when(pricingConfigRepository.findTimeBasedPricing(any(), anyInt()))
            .thenReturn(Optional.empty());

        // Act
        PricingResponse response = dynamicPricingService.calculateDynamicPricing(testShow);

        // Assert
        assertEquals(1.0, response.getTotalMultiplier(), 0.01);
        assertTrue(response.getAppliedFactors().contains("Regular weekday"));
    }

    // ==================== Test 4: Combined Pricing Factors ====================

    @Test
    @Order(9)
    @DisplayName("Test 9: Calculate pricing with ALL factors combined (Weekend + Evening + High Demand)")
    void test09_calculateDynamicPricing_AllFactorsCombined() {
        // Arrange - Weekend, Evening, High Demand
        testShow.setShowDate(LocalDate.now().with(DayOfWeek.SATURDAY));
        testShow.setShowTime(LocalTime.of(19, 0));
        List<ShowSeat> highDemandSeats = createShowSeats(100, 85);
        
        when(showSeatRepository.findAllByShow(testShow)).thenReturn(highDemandSeats);
        
        // Setup all pricing configs
        when(pricingConfigRepository.findDemandBasedPricing(PricingFactorType.DEMAND_BASED, 85))
            .thenReturn(Optional.of(pricingConfigs.get(0))); // HIGH_DEMAND 1.5x
        when(pricingConfigRepository.findTimeBasedPricing(PricingFactorType.TIME_BASED, 19))
            .thenReturn(Optional.of(pricingConfigs.get(5))); // EVENING 1.3x
        when(pricingConfigRepository.findByConfigKeyAndIsActiveTrue("WEEKEND"))
            .thenReturn(Optional.of(pricingConfigs.get(7))); // WEEKEND 1.25x

        // Act
        PricingResponse response = dynamicPricingService.calculateDynamicPricing(testShow);

        // Assert
        // 1.5 * 1.3 * 1.25 = 2.4375
        assertTrue(response.getTotalMultiplier() >= 2.4, 
            "Combined factors should give ~2.4x multiplier");
        assertEquals(3, response.getAppliedFactors().size());
        assertTrue(response.getAppliedFactors().contains("High demand"));
        assertTrue(response.getAppliedFactors().contains("Evening premium"));
        assertTrue(response.getAppliedFactors().contains("Weekend premium"));
        assertTrue(response.getPriceJustification().contains("85%"));
        assertTrue(response.getPriceJustification().contains("Weekend"));
    }

    // ==================== Test 5: Apply Dynamic Pricing ====================

    @Test
    @Order(10)
    @DisplayName("Test 10: Apply dynamic pricing to show seats")
    void test10_applyDynamicPricingToShow_UpdatesSeatPrices() {
        // Arrange
        when(showSeatRepository.findAllByShow(testShow)).thenReturn(showSeats);
        when(pricingConfigRepository.findDemandBasedPricing(any(), anyInt()))
            .thenReturn(Optional.of(pricingConfigs.get(2))); // NORMAL 1.0x
        when(pricingConfigRepository.findTimeBasedPricing(any(), anyInt()))
            .thenReturn(Optional.empty());
        when(pricingConfigRepository.findByConfigKeyAndIsActiveTrue(anyString()))
            .thenReturn(Optional.empty());
        when(showSeatRepository.saveAll(any(List.class))).thenReturn(showSeats);

        // Act
        dynamicPricingService.applyDynamicPricingToShow(testShow);

        // Assert
        verify(showSeatRepository, times(2)).findAllByShow(testShow); // Called twice
        verify(showSeatRepository, times(1)).saveAll(any(List.class));
    }

    // ==================== Test 6: Calculate Individual Seat Price ====================

    @Test
    @Order(11)
    @DisplayName("Test 11: Calculate individual seat price with dynamic factors")
    void test11_calculateSeatPrice_AppliesMultiplier() {
        // Arrange
        int basePrice = 200;
        when(showSeatRepository.findAllByShow(testShow)).thenReturn(showSeats);
        when(pricingConfigRepository.findDemandBasedPricing(any(), anyInt()))
            .thenReturn(Optional.of(pricingConfigs.get(0))); // HIGH_DEMAND 1.5x
        when(pricingConfigRepository.findTimeBasedPricing(any(), anyInt()))
            .thenReturn(Optional.empty());
        when(pricingConfigRepository.findByConfigKeyAndIsActiveTrue(anyString()))
            .thenReturn(Optional.empty());

        // Act
        Integer dynamicPrice = dynamicPricingService.calculateSeatPrice(
            testShow, SeatType.CLASSIC, basePrice);

        // Assert
        assertTrue(dynamicPrice >= 300, "Price should be at least 300 (200 * 1.5)");
    }

    // ==================== Test 7: Pricing Configuration Management ====================

    @Test
    @Order(12)
    @DisplayName("Test 12: Get all pricing configs")
    void test12_getAllPricingConfigs_ReturnsAllConfigs() {
        // Arrange
        when(pricingConfigRepository.findAll()).thenReturn(pricingConfigs);

        // Act
        List<PricingConfig> result = dynamicPricingService.getAllPricingConfigs();

        // Assert
        assertNotNull(result);
        assertEquals(9, result.size());
        verify(pricingConfigRepository, times(1)).findAll();
    }

    @Test
    @Order(13)
    @DisplayName("Test 13: Get active pricing configs only")
    void test13_getActivePricingConfigs_ReturnsOnlyActiveConfigs() {
        // Arrange
        List<PricingConfig> activeConfigs = pricingConfigs.subList(0, 5);
        when(pricingConfigRepository.findByIsActiveTrue()).thenReturn(activeConfigs);

        // Act
        List<PricingConfig> result = dynamicPricingService.getActivePricingConfigs();

        // Assert
        assertNotNull(result);
        assertEquals(5, result.size());
        assertTrue(result.stream().allMatch(PricingConfig::getIsActive));
        verify(pricingConfigRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    @Order(14)
    @DisplayName("Test 14: Update pricing config successfully")
    void test14_updatePricingConfig_UpdatesSuccessfully() throws Exception {
        // Arrange
        PricingConfig existing = pricingConfigs.get(0);
        existing.setConfigId(1);
        
        PricingConfig updated = new PricingConfig();
        updated.setMultiplier(2.0);
        updated.setDescription("Updated description");
        updated.setIsActive(false);

        when(pricingConfigRepository.findById(1)).thenReturn(Optional.of(existing));
        when(pricingConfigRepository.save(any(PricingConfig.class))).thenReturn(existing);

        // Act
        PricingConfig result = dynamicPricingService.updatePricingConfig(1, updated);

        // Assert
        assertNotNull(result);
        verify(pricingConfigRepository, times(1)).findById(1);
        verify(pricingConfigRepository, times(1)).save(any(PricingConfig.class));
    }

    @Test
    @Order(15)
    @DisplayName("Test 15: Update pricing config - Config not found")
    void test15_updatePricingConfig_ConfigNotFound() {
        // Arrange
        when(pricingConfigRepository.findById(999)).thenReturn(Optional.empty());

        PricingConfig updated = new PricingConfig();
        updated.setMultiplier(2.0);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            dynamicPricingService.updatePricingConfig(999, updated);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(pricingConfigRepository, times(1)).findById(999);
        verify(pricingConfigRepository, never()).save(any(PricingConfig.class));
    }


    @Test
    @Order(17)
    @DisplayName("Test 17: Calculate pricing with no matching pricing rules")
    void test17_calculateDynamicPricing_NoMatchingRules() {
        // Arrange
        when(showSeatRepository.findAllByShow(testShow)).thenReturn(showSeats);
        when(pricingConfigRepository.findDemandBasedPricing(any(), anyInt()))
            .thenReturn(Optional.empty());
        when(pricingConfigRepository.findTimeBasedPricing(any(), anyInt()))
            .thenReturn(Optional.empty());
        when(pricingConfigRepository.findByConfigKeyAndIsActiveTrue(anyString()))
            .thenReturn(Optional.empty());

        // Act
        PricingResponse response = dynamicPricingService.calculateDynamicPricing(testShow);

        // Assert
        assertNotNull(response);
        assertEquals(1.0, response.getTotalMultiplier(), 0.01);
        assertTrue(response.getAppliedFactors().isEmpty());
    }

    @Test
    @Order(18)
    @DisplayName("Test 18: Verify pricing response contains all required fields")
    void test18_calculateDynamicPricing_ResponseHasAllFields() {
        // Arrange
        when(showSeatRepository.findAllByShow(testShow)).thenReturn(showSeats);
        when(pricingConfigRepository.findDemandBasedPricing(any(), anyInt()))
            .thenReturn(Optional.of(pricingConfigs.get(2)));
        when(pricingConfigRepository.findTimeBasedPricing(any(), anyInt()))
            .thenReturn(Optional.empty());
        when(pricingConfigRepository.findByConfigKeyAndIsActiveTrue(anyString()))
            .thenReturn(Optional.empty());

        // Act
        PricingResponse response = dynamicPricingService.calculateDynamicPricing(testShow);

        // Assert
        assertNotNull(response.getBasePrices());
        assertNotNull(response.getDynamicPrices());
        assertNotNull(response.getAppliedFactors());
        assertNotNull(response.getTotalMultiplier());
        assertNotNull(response.getPriceJustification());
        assertTrue(response.getBasePrices().containsKey("CLASSIC"));
        assertTrue(response.getBasePrices().containsKey("PREMIUM"));
    }

    @AfterEach
    void tearDown() {
        testShow = null;
        showSeats = null;
        pricingConfigs = null;
    }
}
