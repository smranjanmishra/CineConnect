package com.acciojob.bookmyshowapplication.service;

import com.acciojob.bookmyshowapplication.Enums.RefundStatus;
import com.acciojob.bookmyshowapplication.Enums.TicketStatus;
import com.acciojob.bookmyshowapplication.Models.*;
import com.acciojob.bookmyshowapplication.Repository.*;
import com.acciojob.bookmyshowapplication.Requests.CancelTicketRequest;
import com.acciojob.bookmyshowapplication.Responses.CancellationResponse;
import com.acciojob.bookmyshowapplication.Service.CancellationService;
import com.acciojob.bookmyshowapplication.Service.WaitlistService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CancellationService using Mockito
 * Tests are organized sequentially to test the complete cancellation flow
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Cancellation Service Tests")
class CancellationServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ShowSeatRepository showSeatRepository;

    @Mock
    private RefundTransactionRepository refundTransactionRepository;

    @Mock
    private WaitlistService waitlistService;

    @InjectMocks
    private CancellationService cancellationService;

    private Ticket testTicket;
    private User testUser;
    private Show testShow;
    private Movie testMovie;
    private Theater testTheater;
    private CancelTicketRequest cancelRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        testUser = new User();
        testUser.setUserId(1);
        testUser.setName("John Doe");
        testUser.setEmailId("john@example.com");
        testUser.setMobNo("9876543210");

        testMovie = new Movie();
        testMovie.setMovieId(1);
        testMovie.setMovieName("Inception");

        testTheater = new Theater();
        testTheater.setTheaterId(1);
        testTheater.setName("PVR Cinemas");
        testTheater.setAddress("Mumbai");

        testShow = new Show();
        testShow.setShowId(1);
        testShow.setShowDate(LocalDate.now().plusDays(3));
        testShow.setShowTime(LocalTime.of(18, 0));
        testShow.setMovie(testMovie);
        testShow.setTheater(testTheater);

        testTicket = Ticket.builder()
                .ticketId("ticket-123")
                .user(testUser)
                .movieName("Inception")
                .showDate(LocalDate.now().plusDays(3))
                .showTime(LocalTime.of(18, 0))
                .theaterNameAndAddress("PVR Cinemas Mumbai")
                .totalAmtPaid(1000)
                .ticketStatus(TicketStatus.CONFIRMED)
                .refundStatus(RefundStatus.NOT_APPLICABLE)
                .show(testShow)
                .bookedAt(LocalDateTime.now())
                .build();

        cancelRequest = new CancelTicketRequest();
        cancelRequest.setTicketId("ticket-123");
        cancelRequest.setCancellationReason("Plans changed");
    }

    // ==================== Test 1: Refund Percentage Calculations ====================

    @Test
    @Order(1)
    @DisplayName("Test 1: Calculate 100% refund when cancelled more than 48 hours before show")
    void test01_calculateRefundPercentage_MoreThan48Hours() {
        // Arrange
        LocalDateTime showDateTime = LocalDateTime.now().plusHours(72); // 3 days ahead

        // Act
        double refundPercentage = cancellationService.calculateRefundPercentage(showDateTime);

        // Assert
        assertEquals(1.0, refundPercentage, 0.01, 
            "Should get 100% refund when cancelled more than 48 hours before show");
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Calculate 75% refund when cancelled between 24-48 hours before show")
    void test02_calculateRefundPercentage_Between24And48Hours() {
        // Arrange
        LocalDateTime showDateTime = LocalDateTime.now().plusHours(36); // 1.5 days ahead

        // Act
        double refundPercentage = cancellationService.calculateRefundPercentage(showDateTime);

        // Assert
        assertEquals(0.75, refundPercentage, 0.01, 
            "Should get 75% refund when cancelled between 24-48 hours before show");
    }

    @Test
    @Order(3)
    @DisplayName("Test 3: Calculate 50% refund when cancelled between 12-24 hours before show")
    void test03_calculateRefundPercentage_Between12And24Hours() {
        // Arrange
        LocalDateTime showDateTime = LocalDateTime.now().plusHours(18); // 18 hours ahead

        // Act
        double refundPercentage = cancellationService.calculateRefundPercentage(showDateTime);

        // Assert
        assertEquals(0.50, refundPercentage, 0.01, 
            "Should get 50% refund when cancelled between 12-24 hours before show");
    }

    @Test
    @Order(4)
    @DisplayName("Test 4: Calculate 25% refund when cancelled between 6-12 hours before show")
    void test04_calculateRefundPercentage_Between6And12Hours() {
        // Arrange
        LocalDateTime showDateTime = LocalDateTime.now().plusHours(9); // 9 hours ahead

        // Act
        double refundPercentage = cancellationService.calculateRefundPercentage(showDateTime);

        // Assert
        assertEquals(0.25, refundPercentage, 0.01, 
            "Should get 25% refund when cancelled between 6-12 hours before show");
    }

    @Test
    @Order(5)
    @DisplayName("Test 5: Calculate 0% refund when cancelled less than 6 hours before show")
    void test05_calculateRefundPercentage_LessThan6Hours() {
        // Arrange
        LocalDateTime showDateTime = LocalDateTime.now().plusHours(3); // 3 hours ahead

        // Act
        double refundPercentage = cancellationService.calculateRefundPercentage(showDateTime);

        // Assert
        assertEquals(0.0, refundPercentage, 0.01, 
            "Should get 0% refund when cancelled less than 6 hours before show");
    }

    // ==================== Test 3: Edge Cases and Validations ====================

    @Test
    @Order(8)
    @DisplayName("Test 8: Fail to cancel - Ticket not found")
    void test08_cancelTicket_TicketNotFound() {
        // Arrange
        when(ticketRepository.findById("ticket-123")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            cancellationService.cancelTicket(cancelRequest);
        });

        assertTrue(exception.getMessage().contains("Ticket not found"), 
            "Should throw exception with 'Ticket not found' message");
        verify(ticketRepository, times(1)).findById("ticket-123");
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @Order(9)
    @DisplayName("Test 9: Fail to cancel - Ticket already cancelled")
    void test09_cancelTicket_AlreadyCancelled() {
        // Arrange
        testTicket.setTicketStatus(TicketStatus.CANCELLED);
        when(ticketRepository.findById("ticket-123")).thenReturn(Optional.of(testTicket));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            cancellationService.cancelTicket(cancelRequest);
        });

        assertTrue(exception.getMessage().contains("already cancelled"), 
            "Should throw exception when ticket is already cancelled");
        verify(ticketRepository, times(1)).findById("ticket-123");
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @Order(10)
    @DisplayName("Test 10: Fail to cancel - Show has already passed")
    void test10_cancelTicket_ShowAlreadyPassed() {
        // Arrange
        testTicket.setShowDate(LocalDate.now().minusDays(1)); // Yesterday
        testTicket.setShowTime(LocalTime.of(18, 0));
        when(ticketRepository.findById("ticket-123")).thenReturn(Optional.of(testTicket));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            cancellationService.cancelTicket(cancelRequest);
        });

        assertTrue(exception.getMessage().contains("already passed"), 
            "Should throw exception when show has already passed");
        verify(ticketRepository, times(1)).findById("ticket-123");
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    // ==================== Test 4: Refund Status Queries ====================

    @Test
    @Order(12)
    @DisplayName("Test 12: Get refund status successfully")
    void test12_getRefundStatus_Success() throws Exception {
        // Arrange
        RefundTransaction refundTransaction = RefundTransaction.builder()
                .refundId(1)
                .ticket(testTicket)
                .originalAmount(1000)
                .refundAmount(1000)
                .refundPercentage(1.0)
                .refundStatus(RefundStatus.COMPLETED)
                .build();

        when(refundTransactionRepository.findByTicketTicketId("ticket-123"))
            .thenReturn(Optional.of(refundTransaction));

        // Act
        RefundTransaction result = cancellationService.getRefundStatus("ticket-123");

        // Assert
        assertNotNull(result);
        assertEquals(1000, result.getRefundAmount());
        assertEquals(RefundStatus.COMPLETED, result.getRefundStatus());
        verify(refundTransactionRepository, times(1)).findByTicketTicketId("ticket-123");
    }

    @Test
    @Order(13)
    @DisplayName("Test 13: Fail to get refund status - Transaction not found")
    void test13_getRefundStatus_NotFound() {
        // Arrange
        when(refundTransactionRepository.findByTicketTicketId("ticket-123"))
            .thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            cancellationService.getRefundStatus("ticket-123");
        });

        assertTrue(exception.getMessage().contains("No refund transaction found"));
        verify(refundTransactionRepository, times(1)).findByTicketTicketId("ticket-123");
    }

    // ==================== Test 5: Seat Release Verification ====================

    @Test
    @Order(14)
    @DisplayName("Test 14: Verify seats are released after cancellation")
    void test14_cancelTicket_SeatsReleased() throws Exception {
        // Arrange
        List<ShowSeat> showSeats = new ArrayList<>();
        ShowSeat seat1 = new ShowSeat();
        seat1.setSeatNo("A1");
        seat1.setIsAvailable(false);
        ShowSeat seat2 = new ShowSeat();
        seat2.setSeatNo("A2");
        seat2.setIsAvailable(false);
        showSeats.add(seat1);
        showSeats.add(seat2);

        when(ticketRepository.findById("ticket-123")).thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(refundTransactionRepository.save(any(RefundTransaction.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(showSeatRepository.findAllByShow(any(Show.class))).thenReturn(showSeats);
        when(showSeatRepository.saveAll(any(List.class))).thenReturn(showSeats);
        doNothing().when(waitlistService).processWaitlistForShow(any(Show.class));

        // Act
        cancellationService.cancelTicket(cancelRequest);

        // Assert
        verify(showSeatRepository, times(1)).findAllByShow(testShow);
        verify(showSeatRepository, times(1)).saveAll(any(List.class));
        verify(waitlistService, times(1)).processWaitlistForShow(testShow);
    }

    // ==================== Test 6: Waitlist Integration ====================

    @Test
    @Order(15)
    @DisplayName("Test 15: Verify waitlist is processed after cancellation")
    void test15_cancelTicket_WaitlistProcessed() throws Exception {
        // Arrange
        when(ticketRepository.findById("ticket-123")).thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(refundTransactionRepository.save(any(RefundTransaction.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(showSeatRepository.findAllByShow(any(Show.class))).thenReturn(new ArrayList<>());
        doNothing().when(waitlistService).processWaitlistForShow(any(Show.class));

        // Act
        cancellationService.cancelTicket(cancelRequest);

        // Assert
        verify(waitlistService, times(1)).processWaitlistForShow(testShow);
    }

    @Test
    @Order(16)
    @DisplayName("Test 16: Cancellation succeeds even if waitlist processing fails")
    void test16_cancelTicket_WaitlistFailureDoesNotAffectCancellation() throws Exception {
        // Arrange
        when(ticketRepository.findById("ticket-123")).thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(refundTransactionRepository.save(any(RefundTransaction.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(showSeatRepository.findAllByShow(any(Show.class))).thenReturn(new ArrayList<>());
        doThrow(new RuntimeException("Waitlist processing failed"))
            .when(waitlistService).processWaitlistForShow(any(Show.class));

        // Act - Should not throw exception
        CancellationResponse response = cancellationService.cancelTicket(cancelRequest);

        // Assert
        assertNotNull(response);
        assertEquals(TicketStatus.CANCELLED, response.getTicketStatus());
        verify(waitlistService, times(1)).processWaitlistForShow(testShow);
    }

    @AfterEach
    void tearDown() {
        // Cleanup if needed
        testTicket = null;
        testUser = null;
        testShow = null;
        cancelRequest = null;
    }
}
