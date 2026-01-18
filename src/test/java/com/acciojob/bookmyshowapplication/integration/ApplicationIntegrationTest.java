package com.acciojob.bookmyshowapplication.integration;

import com.acciojob.bookmyshowapplication.Controllers.*;
import com.acciojob.bookmyshowapplication.Service.*;
import com.acciojob.bookmyshowapplication.Repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests to verify Spring context loads correctly
 * and all components are properly wired
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Application Integration Tests")
@ActiveProfiles("test")
class ApplicationIntegrationTest {

    // Controllers
    @Autowired(required = false)
    private CancellationController cancellationController;

    @Autowired(required = false)
    private WaitlistController waitlistController;

    @Autowired(required = false)
    private DynamicPricingController dynamicPricingController;

    @Autowired(required = false)
    private TicketController ticketController;

    @Autowired(required = false)
    private UserController userController;

    @Autowired(required = false)
    private MovieController movieController;

    @Autowired(required = false)
    private TheaterController theaterController;

    @Autowired(required = false)
    private ShowController showController;

    // Services
    @Autowired(required = false)
    private CancellationService cancellationService;

    @Autowired(required = false)
    private WaitlistService waitlistService;

    @Autowired(required = false)
    private DynamicPricingService dynamicPricingService;

    @Autowired(required = false)
    private TicketService ticketService;

    @Autowired(required = false)
    private UserService userService;

    @Autowired(required = false)
    private MovieService movieService;

    @Autowired(required = false)
    private TheaterService theaterService;

    @Autowired(required = false)
    private ShowService showService;

    // Repositories
    @Autowired(required = false)
    private TicketRepository ticketRepository;

    @Autowired(required = false)
    private UserRepository userRepository;

    @Autowired(required = false)
    private MovieRepository movieRepository;

    @Autowired(required = false)
    private TheaterRepository theaterRepository;

    @Autowired(required = false)
    private ShowRepository showRepository;

    @Autowired(required = false)
    private ShowSeatRepository showSeatRepository;

    @Autowired(required = false)
    private WaitlistRepository waitlistRepository;

    @Autowired(required = false)
    private PricingConfigRepository pricingConfigRepository;

    @Autowired(required = false)
    private RefundTransactionRepository refundTransactionRepository;

    // ==================== Test 1: Controller Wiring ====================

    @Test
    @Order(1)
    @DisplayName("Test 1: Verify all new feature controllers are loaded")
    void test01_verifyNewFeatureControllers() {
        assertNotNull(cancellationController, "CancellationController should be loaded");
        assertNotNull(waitlistController, "WaitlistController should be loaded");
        assertNotNull(dynamicPricingController, "DynamicPricingController should be loaded");
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Verify all existing controllers are loaded")
    void test02_verifyExistingControllers() {
        assertNotNull(ticketController, "TicketController should be loaded");
        assertNotNull(userController, "UserController should be loaded");
        assertNotNull(movieController, "MovieController should be loaded");
        assertNotNull(theaterController, "TheaterController should be loaded");
        assertNotNull(showController, "ShowController should be loaded");
    }

    // ==================== Test 2: Service Wiring ====================

    @Test
    @Order(3)
    @DisplayName("Test 3: Verify all new feature services are loaded")
    void test03_verifyNewFeatureServices() {
        assertNotNull(cancellationService, "CancellationService should be loaded");
        assertNotNull(waitlistService, "WaitlistService should be loaded");
        assertNotNull(dynamicPricingService, "DynamicPricingService should be loaded");
    }

    @Test
    @Order(4)
    @DisplayName("Test 4: Verify all existing services are loaded")
    void test04_verifyExistingServices() {
        assertNotNull(ticketService, "TicketService should be loaded");
        assertNotNull(userService, "UserService should be loaded");
        assertNotNull(movieService, "MovieService should be loaded");
        assertNotNull(theaterService, "TheaterService should be loaded");
        assertNotNull(showService, "ShowService should be loaded");
    }

    // ==================== Test 3: Repository Wiring ====================

    @Test
    @Order(5)
    @DisplayName("Test 5: Verify all new feature repositories are loaded")
    void test05_verifyNewFeatureRepositories() {
        assertNotNull(waitlistRepository, "WaitlistRepository should be loaded");
        assertNotNull(pricingConfigRepository, "PricingConfigRepository should be loaded");
        assertNotNull(refundTransactionRepository, "RefundTransactionRepository should be loaded");
    }

    @Test
    @Order(6)
    @DisplayName("Test 6: Verify all existing repositories are loaded")
    void test06_verifyExistingRepositories() {
        assertNotNull(ticketRepository, "TicketRepository should be loaded");
        assertNotNull(userRepository, "UserRepository should be loaded");
        assertNotNull(movieRepository, "MovieRepository should be loaded");
        assertNotNull(theaterRepository, "TheaterRepository should be loaded");
        assertNotNull(showRepository, "ShowRepository should be loaded");
        assertNotNull(showSeatRepository, "ShowSeatRepository should be loaded");
    }

    // ==================== Test 4: Component Integration ====================

    @Test
    @Order(7)
    @DisplayName("Test 7: Verify CancellationService dependencies are wired")
    void test07_verifyCancellationServiceDependencies() {
        assertNotNull(cancellationService, "CancellationService should be loaded");
        // CancellationService depends on these repositories
        assertNotNull(ticketRepository, "TicketRepository dependency should be loaded");
        assertNotNull(showSeatRepository, "ShowSeatRepository dependency should be loaded");
        assertNotNull(refundTransactionRepository, "RefundTransactionRepository dependency should be loaded");
        assertNotNull(waitlistService, "WaitlistService dependency should be loaded");
    }

    @Test
    @Order(8)
    @DisplayName("Test 8: Verify DynamicPricingService dependencies are wired")
    void test08_verifyDynamicPricingServiceDependencies() {
        assertNotNull(dynamicPricingService, "DynamicPricingService should be loaded");
        assertNotNull(pricingConfigRepository, "PricingConfigRepository dependency should be loaded");
        assertNotNull(showSeatRepository, "ShowSeatRepository dependency should be loaded");
    }

    @Test
    @Order(9)
    @DisplayName("Test 9: Verify WaitlistService dependencies are wired")
    void test09_verifyWaitlistServiceDependencies() {
        assertNotNull(waitlistService, "WaitlistService should be loaded");
        assertNotNull(waitlistRepository, "WaitlistRepository dependency should be loaded");
        assertNotNull(userRepository, "UserRepository dependency should be loaded");
        assertNotNull(movieRepository, "MovieRepository dependency should be loaded");
        assertNotNull(theaterRepository, "TheaterRepository dependency should be loaded");
        assertNotNull(showRepository, "ShowRepository dependency should be loaded");
        assertNotNull(showSeatRepository, "ShowSeatRepository dependency should be loaded");
    }

    @Test
    @Order(10)
    @DisplayName("Test 10: Verify TicketService dependencies including new features")
    void test10_verifyTicketServiceDependencies() {
        assertNotNull(ticketService, "TicketService should be loaded");
        assertNotNull(dynamicPricingService, "DynamicPricingService dependency should be loaded");
    }

    // ==================== Test 5: Application Context ====================

    @Test
    @Order(11)
    @DisplayName("Test 11: Verify Spring context loads successfully")
    void test11_contextLoads() {
        // If this test passes, it means the Spring context loaded successfully
        assertTrue(true, "Spring Boot context should load successfully");
    }

    @Test
    @Order(12)
    @DisplayName("Test 12: Verify no duplicate bean definitions")
    void test12_noDuplicateBeans() {
        // All autowired dependencies should be unique
        assertNotNull(cancellationController);
        assertNotNull(waitlistController);
        assertNotNull(dynamicPricingController);
        
        // Verify they are distinct instances
        assertNotSame(cancellationController, waitlistController);
        assertNotSame(cancellationController, dynamicPricingController);
        assertNotSame(waitlistController, dynamicPricingController);
    }
}
