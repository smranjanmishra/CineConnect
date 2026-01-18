# CineConnect - Test Scenarios

## Comprehensive Test Scenarios for New Features

### Scenario 1: Complete Booking and Cancellation Flow

#### Step 1: Create Test Data
```
POST /user/addUser
{
  "name": "John Doe",
  "emailId": "john@example.com",
  "mobNo": "9876543210"
}
```

#### Step 2: Book a Ticket
```
POST /ticket/bookTicket
{
  "movieName": "Inception",
  "showDate": "2026-01-25",
  "showTime": "18:00:00",
  "requestedSeats": ["A1", "A2"],
  "theaterId": 1,
  "mobNo": "9876543210"
}
```
Expected: Ticket booked successfully, receive ticketId

#### Step 3: Cancel Ticket (More than 48 hours before show)
```
POST /api/cancellation/cancel
{
  "ticketId": "<ticket-id-from-step-2>",
  "cancellationReason": "Plans changed"
}
```
Expected: 100% refund, status CANCELLED

#### Step 4: Check Refund Status
```
GET /api/cancellation/refund-status/<ticket-id>
```
Expected: Refund status COMPLETED

---

### Scenario 2: Dynamic Pricing Based on Demand

#### Step 1: Check Initial Pricing (Low Demand)
```
GET /api/pricing/show/1
```
Expected: Base prices with normal multiplier (1.0)

#### Step 2: Book Multiple Tickets to Increase Occupancy
Book tickets until >70% seats are occupied

#### Step 3: Check Pricing Again (High Demand)
```
GET /api/pricing/show/1
```
Expected: Prices increased with high demand multiplier (1.5x)

#### Step 4: Apply Dynamic Pricing
```
POST /api/pricing/apply/1
```
Expected: All seat prices updated

---

### Scenario 3: Time-Based Dynamic Pricing

#### Test Morning Show Discount
```
GET /api/pricing/show/<morning-show-id>
```
Expected: 20% discount (0.8x multiplier) for shows between 6 AM - 12 PM

#### Test Evening Show Premium
```
GET /api/pricing/show/<evening-show-id>
```
Expected: 30% premium (1.3x multiplier) for shows between 6 PM - 10 PM

---

### Scenario 4: Weekend Premium Pricing

#### Create Weekend Show
```
POST /show/addShow
{
  "showDate": "2026-01-25",  // Saturday
  "showTime": "18:00:00",
  "movieId": 1,
  "theaterId": 1
}
```

#### Check Pricing
```
GET /api/pricing/show/<show-id>
```
Expected: 25% weekend premium (1.25x multiplier)

---

### Scenario 5: Waitlist Management

#### Step 1: Fill All Seats
Book all available seats for a show

#### Step 2: Add User to Waitlist
```
POST /api/waitlist/add
{
  "mobNo": "9876543210",
  "theaterId": 1,
  "movieName": "Inception",
  "showDate": "2026-01-25",
  "showTime": "18:00:00",
  "requestedSeatType": "PREMIUM",
  "numberOfSeats": 2
}
```
Expected: Added to waitlist, position in queue returned

#### Step 3: Cancel a Ticket
```
POST /api/cancellation/cancel
{
  "ticketId": "<existing-ticket-id>",
  "cancellationReason": "Emergency"
}
```
Expected: Ticket cancelled, waitlisted users notified (check console logs)

#### Step 4: Check Waitlist Status
```
GET /api/waitlist/user/1
```
Expected: Waitlist status updated to NOTIFIED

---

### Scenario 6: Combined Pricing Factors

#### Create a Weekend Evening Show with High Demand
1. Show Date: Saturday
2. Show Time: 7:00 PM
3. Occupancy: >70%

#### Check Pricing
```
GET /api/pricing/show/<show-id>
```
Expected: Multiple multipliers applied:
- High demand: 1.5x
- Evening show: 1.3x
- Weekend: 1.25x
- Total: ~2.4x base price

---

### Scenario 7: Refund Policy Testing

#### Test 1: Cancel 50 hours before show
Expected: 100% refund

#### Test 2: Cancel 30 hours before show
Expected: 75% refund

#### Test 3: Cancel 15 hours before show
Expected: 50% refund

#### Test 4: Cancel 8 hours before show
Expected: 25% refund

#### Test 5: Cancel 3 hours before show
Expected: No refund (0%)

---

### Scenario 8: Waitlist Priority (FIFO)

#### Step 1: Add Multiple Users to Waitlist
Add 3 users in sequence:
1. User A at 10:00 AM
2. User B at 10:05 AM
3. User C at 10:10 AM

#### Step 2: Cancel Tickets
Cancel 2 tickets

#### Step 3: Verify Notification Order
Expected: User A and User B notified first (FIFO order)

---

### Scenario 9: Pricing Configuration Management

#### Step 1: Get All Pricing Configs
```
GET /api/pricing/configs
```
Expected: List of all pricing rules

#### Step 2: Update a Config
```
PUT /api/pricing/configs/1
{
  "multiplier": 1.8,
  "description": "Updated high demand pricing",
  "isActive": true
}
```
Expected: Config updated successfully

#### Step 3: Verify Updated Pricing
```
GET /api/pricing/show/<show-id>
```
Expected: New multiplier applied

---

### Scenario 10: Edge Cases

#### Test 1: Cancel Already Cancelled Ticket
Expected: Error message "Ticket is already cancelled"

#### Test 2: Cancel Past Show Ticket
Expected: Error message "Cannot cancel ticket for a show that has already passed"

#### Test 3: Add to Waitlist for Past Show
Expected: Error message "Cannot add to waitlist for a show that has already passed"

#### Test 4: Duplicate Waitlist Entry
Expected: Error message "You are already on the waitlist for this show"

#### Test 5: Get Refund Status for Non-Cancelled Ticket
Expected: Error message "No refund transaction found"

---

## Performance Testing

### Load Test: Concurrent Bookings
- Simulate 100 concurrent booking requests
- Verify seat availability is correctly managed
- Ensure no double bookings occur

### Load Test: Dynamic Pricing Updates
- Book tickets rapidly to change occupancy
- Verify pricing updates correctly
- Check for race conditions

### Load Test: Waitlist Processing
- Add 1000 users to waitlist
- Cancel tickets
- Verify correct FIFO processing

---

## Integration Testing

### Test 1: Full User Journey
1. User registers
2. Checks show pricing
3. Books tickets
4. Cancels tickets
5. Gets refund
6. Joins waitlist for another show
7. Gets notified when seats available

### Test 2: Admin Operations
1. Create pricing rules
2. Apply dynamic pricing to shows
3. Monitor refund transactions
4. View waitlist statistics

---

## Automated Test Execution

Run the test suite:
```bash
mvn test
```

Tests included:
- `CancellationServiceTest` - 6 test cases
- `DynamicPricingServiceTest` - 10 test cases
- `WaitlistServiceTest` - 4 test cases
- `IntegrationTest` - Context loading test

Expected: All tests should pass ✓

---

## Monitoring and Logs

### Key Log Messages to Watch For:

1. **Waitlist Notification**
```
=== WAITLIST NOTIFICATION ===
To: user@example.com
Subject: Seats Available for Your Waitlisted Show!
```

2. **Dynamic Pricing Applied**
```
Dynamic pricing applied successfully
```

3. **Refund Processing**
```
Refund status: COMPLETED
```

4. **Waitlist Expiry**
```
Expired X waitlist entries
```

---

## Success Criteria

✓ All cancellations process correctly with appropriate refunds
✓ Dynamic pricing updates based on demand, time, and day
✓ Waitlist notifications sent in FIFO order
✓ No double bookings or race conditions
✓ All edge cases handled gracefully
✓ API responses are fast (<500ms)
✓ Database transactions are atomic
✓ All unit tests pass
✓ Integration tests pass
