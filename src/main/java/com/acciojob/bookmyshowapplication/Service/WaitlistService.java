package com.acciojob.bookmyshowapplication.Service;

import com.acciojob.bookmyshowapplication.Enums.SeatType;
import com.acciojob.bookmyshowapplication.Enums.WaitlistStatus;
import com.acciojob.bookmyshowapplication.Models.*;
import com.acciojob.bookmyshowapplication.Repository.*;
import com.acciojob.bookmyshowapplication.Requests.AddToWaitlistRequest;
import com.acciojob.bookmyshowapplication.Responses.WaitlistResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WaitlistService {

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;

    /**
     * Add user to waitlist when seats are unavailable
     */
    @Transactional
    public WaitlistResponse addToWaitlist(AddToWaitlistRequest request) throws Exception {
        // Find user
        User user = userRepository.findUserByMobNo(request.getMobNo());
        if (user == null) {
            throw new Exception("User not found with mobile number: " + request.getMobNo());
        }

        // Find show
        Movie movie = movieRepository.findMovieByMovieName(request.getMovieName());
        Theater theater = theaterRepository.findById(request.getTheaterId())
                .orElseThrow(() -> new Exception("Theater not found"));

        Show show = showRepository.findShowByShowDateAndShowTimeAndMovieAndTheater(
                request.getShowDate(),
                request.getShowTime(),
                movie,
                theater
        );

        if (show == null) {
            throw new Exception("Show not found");
        }

        // Check if show has already passed
        LocalDateTime showDateTime = LocalDateTime.of(show.getShowDate(), show.getShowTime());
        if (LocalDateTime.now().isAfter(showDateTime)) {
            throw new Exception("Cannot add to waitlist for a show that has already passed");
        }

        // Check if user already has active waitlist for this show
        List<Waitlist> existingWaitlists = waitlistRepository.findByShowAndStatus(show, WaitlistStatus.PENDING);
        boolean userAlreadyWaitlisted = existingWaitlists.stream()
                .anyMatch(w -> w.getUser().getUserId().equals(user.getUserId()));

        if (userAlreadyWaitlisted) {
            throw new Exception("You are already on the waitlist for this show");
        }

        // Create waitlist entry
        Waitlist waitlist = Waitlist.builder()
                .user(user)
                .show(show)
                .requestedSeatType(request.getRequestedSeatType())
                .numberOfSeats(request.getNumberOfSeats())
                .status(WaitlistStatus.PENDING)
                .expiresAt(showDateTime.minusHours(1)) // Expire 1 hour before show
                .build();

        waitlist = waitlistRepository.save(waitlist);

        // Calculate position in queue
        List<Waitlist> allWaitlists = waitlistRepository.findByShowAndStatusOrderByCreatedAtAsc(
                show, WaitlistStatus.PENDING);
        int position = allWaitlists.indexOf(waitlist) + 1;

        return WaitlistResponse.builder()
                .waitlistId(waitlist.getWaitlistId())
                .status(waitlist.getStatus())
                .movieName(movie.getMovieName())
                .theaterName(theater.getName())
                .showDateTime(showDateTime.toString())
                .requestedSeatType(waitlist.getRequestedSeatType())
                .numberOfSeats(waitlist.getNumberOfSeats())
                .positionInQueue(position)
                .createdAt(waitlist.getCreatedAt())
                .message("You have been added to the waitlist. You will be notified when seats become available.")
                .build();
    }

    /**
     * Process waitlist when seats become available (called after cancellation)
     */
    @Transactional
    public void processWaitlistForShow(Show show) {
        // Get all pending waitlists for this show (ordered by creation time - FIFO)
        List<Waitlist> pendingWaitlists = waitlistRepository
                .findByShowAndStatusOrderByCreatedAtAsc(show, WaitlistStatus.PENDING);

        if (pendingWaitlists.isEmpty()) {
            return;
        }

        // Get available seats
        List<ShowSeat> availableSeats = showSeatRepository.findAllByShow(show).stream()
                .filter(ShowSeat::getIsAvailable)
                .collect(Collectors.toList());

        // Process each waitlist entry
        for (Waitlist waitlist : pendingWaitlists) {
            try {
                // Check if enough seats of requested type are available
                SeatType requestedType = SeatType.valueOf(waitlist.getRequestedSeatType());
                List<ShowSeat> matchingSeats = availableSeats.stream()
                        .filter(seat -> seat.getSeatType() == requestedType)
                        .limit(waitlist.getNumberOfSeats())
                        .collect(Collectors.toList());

                if (matchingSeats.size() >= waitlist.getNumberOfSeats()) {
                    // Notify user
                    notifyWaitlistedUser(waitlist, matchingSeats);
                    
                    // Update waitlist status
                    waitlist.setStatus(WaitlistStatus.NOTIFIED);
                    waitlist.setNotifiedAt(LocalDateTime.now());
                    waitlist.setNotificationMessage(
                            String.format("Seats are now available for your waitlisted show! " +
                                    "Please book within 15 minutes. Movie: %s, Show: %s at %s",
                                    show.getMovie().getMovieName(),
                                    show.getShowDate(),
                                    show.getShowTime())
                    );
                    waitlistRepository.save(waitlist);

                    // Remove these seats from available pool for next iteration
                    availableSeats.removeAll(matchingSeats);
                }
            } catch (Exception e) {
                System.err.println("Error processing waitlist ID " + waitlist.getWaitlistId() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Notify waitlisted user (via email/SMS)
     * In production, this would integrate with notification service
     */
    private void notifyWaitlistedUser(Waitlist waitlist, List<ShowSeat> availableSeats) {
        // Simulate notification
        System.out.println("=== WAITLIST NOTIFICATION ===");
        System.out.println("To: " + waitlist.getUser().getEmailId());
        System.out.println("Subject: Seats Available for Your Waitlisted Show!");
        System.out.println("Message: " + waitlist.getNotificationMessage());
        System.out.println("Available Seats: " + availableSeats.stream()
                .map(ShowSeat::getSeatNo)
                .collect(Collectors.joining(", ")));
        System.out.println("============================");

        // In production:
        // - Send email via EmailService
        // - Send SMS via SMS Gateway
        // - Send push notification
        // - Create in-app notification
    }

    /**
     * Scheduled job to expire old waitlist entries
     * Runs every hour
     */
    @Scheduled(fixedRate = 3600000) // Every hour
    public void expireOldWaitlistEntries() {
        LocalDateTime now = LocalDateTime.now();
        List<Waitlist> expiredWaitlists = waitlistRepository
                .findExpiredWaitlists(WaitlistStatus.PENDING, now);

        for (Waitlist waitlist : expiredWaitlists) {
            waitlist.setStatus(WaitlistStatus.EXPIRED);
            waitlistRepository.save(waitlist);
        }

        if (!expiredWaitlists.isEmpty()) {
            System.out.println("Expired " + expiredWaitlists.size() + " waitlist entries");
        }
    }

    /**
     * Cancel waitlist entry
     */
    @Transactional
    public void cancelWaitlistEntry(Integer waitlistId) throws Exception {
        Waitlist waitlist = waitlistRepository.findById(waitlistId)
                .orElseThrow(() -> new Exception("Waitlist entry not found"));

        if (waitlist.getStatus() != WaitlistStatus.PENDING && waitlist.getStatus() != WaitlistStatus.NOTIFIED) {
            throw new Exception("Cannot cancel waitlist entry with status: " + waitlist.getStatus());
        }

        waitlist.setStatus(WaitlistStatus.CANCELLED);
        waitlistRepository.save(waitlist);
    }

    /**
     * Get user's waitlist entries
     */
    public List<WaitlistResponse> getUserWaitlists(Integer userId) {
        List<Waitlist> waitlists = waitlistRepository.findByUserUserIdAndStatus(userId, WaitlistStatus.PENDING);

        return waitlists.stream().map(waitlist -> {
            List<Waitlist> allWaitlists = waitlistRepository
                    .findByShowAndStatusOrderByCreatedAtAsc(waitlist.getShow(), WaitlistStatus.PENDING);
            int position = allWaitlists.indexOf(waitlist) + 1;

            return WaitlistResponse.builder()
                    .waitlistId(waitlist.getWaitlistId())
                    .status(waitlist.getStatus())
                    .movieName(waitlist.getShow().getMovie().getMovieName())
                    .theaterName(waitlist.getShow().getTheater().getName())
                    .showDateTime(LocalDateTime.of(
                            waitlist.getShow().getShowDate(),
                            waitlist.getShow().getShowTime()).toString())
                    .requestedSeatType(waitlist.getRequestedSeatType())
                    .numberOfSeats(waitlist.getNumberOfSeats())
                    .positionInQueue(position)
                    .createdAt(waitlist.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * Get waitlist stats for a show
     */
    public Long getWaitlistCount(Show show) {
        return waitlistRepository.countByShowAndStatus(show, WaitlistStatus.PENDING);
    }
}
