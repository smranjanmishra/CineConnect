package com.acciojob.bookmyshowapplication.service;

import com.acciojob.bookmyshowapplication.Enums.WaitlistStatus;
import com.acciojob.bookmyshowapplication.Models.*;
import com.acciojob.bookmyshowapplication.Repository.*;
import com.acciojob.bookmyshowapplication.Requests.AddToWaitlistRequest;
import com.acciojob.bookmyshowapplication.Responses.WaitlistResponse;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WaitlistService using Mockito
 * Tests cover waitlist creation, processing, and FIFO queue management
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Waitlist Service Tests")
class WaitlistServiceTest {

    @Mock
    private WaitlistRepository waitlistRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private TheaterRepository theaterRepository;

    @Mock
    private ShowRepository showRepository;

    @Mock
    private ShowSeatRepository showSeatRepository;

    @InjectMocks
    private WaitlistService waitlistService;

    private User testUser;
    private Movie testMovie;
    private Theater testTheater;
    private Show testShow;
    private AddToWaitlistRequest waitlistRequest;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setUserId(1);
        testUser.setName("John Doe");
        testUser.setEmailId("john@example.com");
        testUser.setMobNo("9876543210");

        // Setup test movie
        testMovie = new Movie();
        testMovie.setMovieId(1);
        testMovie.setMovieName("Inception");

        // Setup test theater
        testTheater = new Theater();
        testTheater.setTheaterId(1);
        testTheater.setName("PVR Cinemas");
        testTheater.setAddress("Mumbai");

        // Setup test show
        testShow = new Show();
        testShow.setShowId(1);
        testShow.setShowDate(LocalDate.now().plusDays(2));
        testShow.setShowTime(LocalTime.of(18, 0));
        testShow.setMovie(testMovie);
        testShow.setTheater(testTheater);

        // Setup waitlist request
        waitlistRequest = new AddToWaitlistRequest();
        waitlistRequest.setMobNo("9876543210");
        waitlistRequest.setMovieName("Inception");
        waitlistRequest.setTheaterId(1);
        waitlistRequest.setShowDate(LocalDate.now().plusDays(2));
        waitlistRequest.setShowTime(LocalTime.of(18, 0));
        waitlistRequest.setRequestedSeatType("PREMIUM");
        waitlistRequest.setNumberOfSeats(2);
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Add user to waitlist - Position in queue is correct")
    void test02_addToWaitlist_CorrectQueuePosition() throws Exception {
        // Arrange
        List<Waitlist> existingWaitlists = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Waitlist w = new Waitlist();
            w.setWaitlistId(i + 1);
            w.setUser(testUser);
            existingWaitlists.add(w);
        }

        when(userRepository.findUserByMobNo("9876543210")).thenReturn(testUser);
        when(movieRepository.findMovieByMovieName("Inception")).thenReturn(testMovie);
        when(theaterRepository.findById(1)).thenReturn(Optional.of(testTheater));
        when(showRepository.findShowByShowDateAndShowTimeAndMovieAndTheater(
            any(), any(), any(), any())).thenReturn(testShow);
        when(waitlistRepository.findByShowAndStatus(testShow, WaitlistStatus.PENDING))
            .thenReturn(new ArrayList<>());
        
        Waitlist newWaitlist = new Waitlist();
        newWaitlist.setWaitlistId(4);
        newWaitlist.setCreatedAt(LocalDateTime.now());
        when(waitlistRepository.save(any(Waitlist.class))).thenReturn(newWaitlist);
        
        existingWaitlists.add(newWaitlist);
        when(waitlistRepository.findByShowAndStatusOrderByCreatedAtAsc(testShow, WaitlistStatus.PENDING))
            .thenReturn(existingWaitlists);

        // Act
        WaitlistResponse response = waitlistService.addToWaitlist(waitlistRequest);

        // Assert
        assertEquals(4, response.getPositionInQueue(), 
            "Should be 4th in queue after 3 existing entries");
    }

    // ==================== Test 2: Add to Waitlist - Failure Cases ====================

    @Test
    @Order(3)
    @DisplayName("Test 3: Add to waitlist - User not found")
    void test03_addToWaitlist_UserNotFound() {
        // Arrange
        when(userRepository.findUserByMobNo("9876543210")).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            waitlistService.addToWaitlist(waitlistRequest);
        });

        assertTrue(exception.getMessage().contains("User not found"));
        verify(userRepository, times(1)).findUserByMobNo("9876543210");
        verify(waitlistRepository, never()).save(any(Waitlist.class));
    }

    @Test
    @Order(4)
    @DisplayName("Test 4: Add to waitlist - Theater not found")
    void test04_addToWaitlist_TheaterNotFound() {
        // Arrange
        when(userRepository.findUserByMobNo("9876543210")).thenReturn(testUser);
        when(movieRepository.findMovieByMovieName("Inception")).thenReturn(testMovie);
        when(theaterRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            waitlistService.addToWaitlist(waitlistRequest);
        });

        assertTrue(exception.getMessage().contains("Theater not found"));
        verify(waitlistRepository, never()).save(any(Waitlist.class));
    }

    @Test
    @Order(5)
    @DisplayName("Test 5: Add to waitlist - Show not found")
    void test05_addToWaitlist_ShowNotFound() {
        // Arrange
        when(userRepository.findUserByMobNo("9876543210")).thenReturn(testUser);
        when(movieRepository.findMovieByMovieName("Inception")).thenReturn(testMovie);
        when(theaterRepository.findById(1)).thenReturn(Optional.of(testTheater));
        when(showRepository.findShowByShowDateAndShowTimeAndMovieAndTheater(
            any(), any(), any(), any())).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            waitlistService.addToWaitlist(waitlistRequest);
        });

        assertTrue(exception.getMessage().contains("Show not found"));
        verify(waitlistRepository, never()).save(any(Waitlist.class));
    }

    @Test
    @Order(6)
    @DisplayName("Test 6: Add to waitlist - Show already passed")
    void test06_addToWaitlist_ShowAlreadyPassed() {
        // Arrange
        testShow.setShowDate(LocalDate.now().minusDays(1));
        testShow.setShowTime(LocalTime.of(18, 0));

        when(userRepository.findUserByMobNo("9876543210")).thenReturn(testUser);
        when(movieRepository.findMovieByMovieName("Inception")).thenReturn(testMovie);
        when(theaterRepository.findById(1)).thenReturn(Optional.of(testTheater));
        when(showRepository.findShowByShowDateAndShowTimeAndMovieAndTheater(
            any(), any(), any(), any())).thenReturn(testShow);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            waitlistService.addToWaitlist(waitlistRequest);
        });

        assertTrue(exception.getMessage().contains("already passed"));
        verify(waitlistRepository, never()).save(any(Waitlist.class));
    }

    @Test
    @Order(7)
    @DisplayName("Test 7: Add to waitlist - User already waitlisted for same show")
    void test07_addToWaitlist_AlreadyWaitlisted() {
        // Arrange
        Waitlist existingWaitlist = new Waitlist();
        existingWaitlist.setUser(testUser);
        existingWaitlist.setWaitlistId(1);

        when(userRepository.findUserByMobNo("9876543210")).thenReturn(testUser);
        when(movieRepository.findMovieByMovieName("Inception")).thenReturn(testMovie);
        when(theaterRepository.findById(1)).thenReturn(Optional.of(testTheater));
        when(showRepository.findShowByShowDateAndShowTimeAndMovieAndTheater(
            any(), any(), any(), any())).thenReturn(testShow);
        when(waitlistRepository.findByShowAndStatus(testShow, WaitlistStatus.PENDING))
            .thenReturn(Arrays.asList(existingWaitlist));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            waitlistService.addToWaitlist(waitlistRequest);
        });

        assertTrue(exception.getMessage().contains("already on the waitlist"));
        verify(waitlistRepository, never()).save(any(Waitlist.class));
    }

    // ==================== Test 3: Cancel Waitlist Entry ====================

    @Test
    @Order(8)
    @DisplayName("Test 8: Successfully cancel waitlist entry")
    void test08_cancelWaitlistEntry_Success() throws Exception {
        // Arrange
        Waitlist waitlist = new Waitlist();
        waitlist.setWaitlistId(1);
        waitlist.setStatus(WaitlistStatus.PENDING);
        waitlist.setUser(testUser);

        when(waitlistRepository.findById(1)).thenReturn(Optional.of(waitlist));
        when(waitlistRepository.save(any(Waitlist.class))).thenReturn(waitlist);

        // Act
        waitlistService.cancelWaitlistEntry(1);

        // Assert
        verify(waitlistRepository, times(1)).findById(1);
        verify(waitlistRepository, times(1)).save(any(Waitlist.class));
    }

    @Test
    @Order(9)
    @DisplayName("Test 9: Cancel waitlist - Entry not found")
    void test09_cancelWaitlistEntry_NotFound() {
        // Arrange
        when(waitlistRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            waitlistService.cancelWaitlistEntry(999);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(waitlistRepository, times(1)).findById(999);
        verify(waitlistRepository, never()).save(any(Waitlist.class));
    }

    @Test
    @Order(10)
    @DisplayName("Test 10: Cancel waitlist - Cannot cancel completed entry")
    void test10_cancelWaitlistEntry_AlreadyConverted() {
        // Arrange
        Waitlist waitlist = new Waitlist();
        waitlist.setWaitlistId(1);
        waitlist.setStatus(WaitlistStatus.CONVERTED);

        when(waitlistRepository.findById(1)).thenReturn(Optional.of(waitlist));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            waitlistService.cancelWaitlistEntry(1);
        });

        assertTrue(exception.getMessage().contains("Cannot cancel"));
        verify(waitlistRepository, never()).save(any(Waitlist.class));
    }

    // ==================== Test 4: Get User Waitlists ====================

    @Test
    @Order(11)
    @DisplayName("Test 11: Get user waitlists - Returns list successfully")
    void test11_getUserWaitlists_Success() {
        // Arrange
        Waitlist waitlist1 = createWaitlist(1, testUser, testShow);
        Waitlist waitlist2 = createWaitlist(2, testUser, testShow);
        
        List<Waitlist> userWaitlists = Arrays.asList(waitlist1, waitlist2);
        when(waitlistRepository.findByUserUserIdAndStatus(1, WaitlistStatus.PENDING))
            .thenReturn(userWaitlists);
        when(waitlistRepository.findByShowAndStatusOrderByCreatedAtAsc(testShow, WaitlistStatus.PENDING))
            .thenReturn(userWaitlists);

        // Act
        List<WaitlistResponse> responses = waitlistService.getUserWaitlists(1);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(waitlistRepository, times(1)).findByUserUserIdAndStatus(1, WaitlistStatus.PENDING);
    }

    @Test
    @Order(12)
    @DisplayName("Test 12: Get user waitlists - Empty list for user with no waitlists")
    void test12_getUserWaitlists_EmptyList() {
        // Arrange
        when(waitlistRepository.findByUserUserIdAndStatus(1, WaitlistStatus.PENDING))
            .thenReturn(new ArrayList<>());

        // Act
        List<WaitlistResponse> responses = waitlistService.getUserWaitlists(1);

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(waitlistRepository, times(1)).findByUserUserIdAndStatus(1, WaitlistStatus.PENDING);
    }

    // ==================== Test 5: Get Waitlist Count ====================

    @Test
    @Order(13)
    @DisplayName("Test 13: Get waitlist count for show")
    void test13_getWaitlistCount_ReturnsCorrectCount() {
        // Arrange
        when(waitlistRepository.countByShowAndStatus(testShow, WaitlistStatus.PENDING))
            .thenReturn(5L);

        // Act
        Long count = waitlistService.getWaitlistCount(testShow);

        // Assert
        assertEquals(5L, count);
        verify(waitlistRepository, times(1))
            .countByShowAndStatus(testShow, WaitlistStatus.PENDING);
    }

    @Test
    @Order(14)
    @DisplayName("Test 14: Get waitlist count - Returns zero for show with no waitlist")
    void test14_getWaitlistCount_ZeroForEmptyWaitlist() {
        // Arrange
        when(waitlistRepository.countByShowAndStatus(testShow, WaitlistStatus.PENDING))
            .thenReturn(0L);

        // Act
        Long count = waitlistService.getWaitlistCount(testShow);

        // Assert
        assertEquals(0L, count);
    }

    // ==================== Test 6: Process Waitlist ====================

    @Test
    @Order(15)
    @DisplayName("Test 15: Process waitlist - No pending entries")
    void test15_processWaitlistForShow_NoPendingEntries() {
        // Arrange
        when(waitlistRepository.findByShowAndStatusOrderByCreatedAtAsc(
            testShow, WaitlistStatus.PENDING)).thenReturn(new ArrayList<>());

        // Act - Should not throw exception
        assertDoesNotThrow(() -> {
            waitlistService.processWaitlistForShow(testShow);
        });

        // Assert
        verify(waitlistRepository, times(1))
            .findByShowAndStatusOrderByCreatedAtAsc(testShow, WaitlistStatus.PENDING);
        verify(showSeatRepository, never()).findAllByShow(any());
    }

    @Test
    @Order(16)
    @DisplayName("Test 16: Process waitlist - Notifies users when seats available")
    void test16_processWaitlistForShow_NotifiesUsers() {
        // Arrange
        Waitlist waitlist1 = createWaitlist(1, testUser, testShow);
        waitlist1.setRequestedSeatType("PREMIUM");
        waitlist1.setNumberOfSeats(2);

        when(waitlistRepository.findByShowAndStatusOrderByCreatedAtAsc(
            testShow, WaitlistStatus.PENDING)).thenReturn(Arrays.asList(waitlist1));
        
        List<ShowSeat> availableSeats = createAvailableSeats(5);
        when(showSeatRepository.findAllByShow(testShow)).thenReturn(availableSeats);
        when(waitlistRepository.save(any(Waitlist.class))).thenReturn(waitlist1);

        // Act
        waitlistService.processWaitlistForShow(testShow);

        // Assert
        verify(waitlistRepository, times(1))
            .findByShowAndStatusOrderByCreatedAtAsc(testShow, WaitlistStatus.PENDING);
        verify(showSeatRepository, times(1)).findAllByShow(testShow);
        verify(waitlistRepository, times(1)).save(any(Waitlist.class));
    }

    // ==================== Test 7: Expire Old Waitlists ====================

    @Test
    @Order(17)
    @DisplayName("Test 17: Expire old waitlist entries")
    void test17_expireOldWaitlistEntries_ExpiresCorrectly() {
        // Arrange
        Waitlist expiredWaitlist = createWaitlist(1, testUser, testShow);
        expiredWaitlist.setExpiresAt(LocalDateTime.now().minusHours(1));
        
        when(waitlistRepository.findExpiredWaitlists(
            eq(WaitlistStatus.PENDING), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(expiredWaitlist));
        when(waitlistRepository.save(any(Waitlist.class))).thenReturn(expiredWaitlist);

        // Act
        waitlistService.expireOldWaitlistEntries();

        // Assert
        verify(waitlistRepository, times(1))
            .findExpiredWaitlists(any(WaitlistStatus.class), any(LocalDateTime.class));
        verify(waitlistRepository, times(1)).save(any(Waitlist.class));
    }

    @Test
    @Order(18)
    @DisplayName("Test 18: Expire waitlists - No expired entries")
    void test18_expireOldWaitlistEntries_NoExpiredEntries() {
        // Arrange
        when(waitlistRepository.findExpiredWaitlists(
            any(WaitlistStatus.class), any(LocalDateTime.class)))
            .thenReturn(new ArrayList<>());

        // Act
        waitlistService.expireOldWaitlistEntries();

        // Assert
        verify(waitlistRepository, times(1))
            .findExpiredWaitlists(any(WaitlistStatus.class), any(LocalDateTime.class));
        verify(waitlistRepository, never()).save(any(Waitlist.class));
    }

    // ==================== Helper Methods ====================

    private Waitlist createWaitlist(int id, User user, Show show) {
        Waitlist waitlist = new Waitlist();
        waitlist.setWaitlistId(id);
        waitlist.setUser(user);
        waitlist.setShow(show);
        waitlist.setStatus(WaitlistStatus.PENDING);
        waitlist.setRequestedSeatType("PREMIUM");
        waitlist.setNumberOfSeats(2);
        waitlist.setCreatedAt(LocalDateTime.now());
        waitlist.setExpiresAt(LocalDateTime.now().plusDays(1));
        return waitlist;
    }

    private List<ShowSeat> createAvailableSeats(int count) {
        List<ShowSeat> seats = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ShowSeat seat = new ShowSeat();
            seat.setShowSeatId(i + 1);
            seat.setSeatNo("P" + (i + 1));
            seat.setSeatType(com.acciojob.bookmyshowapplication.Enums.SeatType.PREMIUM);
            seat.setPrice(400);
            seat.setIsAvailable(true);
            seat.setShow(testShow);
            seats.add(seat);
        }
        return seats;
    }

    @AfterEach
    void tearDown() {
        testUser = null;
        testMovie = null;
        testTheater = null;
        testShow = null;
        waitlistRequest = null;
    }
}
