# 📚 CineConnect API Documentation

## Base URL
```
http://localhost:8080/api/v1
```

## Authentication
Currently, authentication is not implemented. All endpoints are publicly accessible.

---

## 👤 User Management APIs

### Create User
```http
POST /api/v1/users
```

**Request Body:**
```json
{
  "name": "John Doe",
  "emailId": "john.doe@example.com",
  "mobNo": "9876543210"
}
```

**Validation Rules:**
- `name`: Required, 2-100 characters
- `emailId`: Required, valid email format
- `mobNo`: Required, exactly 10 digits

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "User created successfully",
  "data": "User has been saved to the database with userId: 1"
}
```

### Get User by ID
```http
GET /api/v1/users/{userId}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "userId": 1,
    "name": "John Doe",
    "emailId": "john.doe@example.com",
    "mobNo": "9876543210"
  }
}
```

### Get User by Mobile
```http
GET /api/v1/users/mobile/{mobNo}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "userId": 1,
    "name": "John Doe",
    "emailId": "john.doe@example.com",
    "mobNo": "9876543210"
  }
}
```

---

## 🎬 Movie Management APIs

### Create Movie
```http
POST /api/v1/movies
```

**Request Body:**
```json
{
  "movieName": "Inception",
  "duration": 148,
  "genre": "SCIFI",
  "releaseDate": "2010-07-16",
  "rating": 8.8,
  "language": "ENGLISH"
}
```

**Validation Rules:**
- `movieName`: Required, 1-200 characters
- `duration`: Required, must be positive
- `genre`: Required, valid enum (HORROR, ROMANTIC, COMEDY, BIOGRAPHY, DRAMA, ACTION, THRILLER, SCIFI, FICTIONAL)
- `releaseDate`: Required, must be in past or present
- `rating`: Required, 0-10
- `language`: Required, valid enum (ENGLISH, HINDI, TELUGU, TAMIL, etc.)

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Movie added successfully",
  "data": "Movie has been saved to the database with movieId: 1"
}
```

### Update Movie
```http
PUT /api/v1/movies/{movieId}
```

**Request Body:**
```json
{
  "movieId": 1,
  "duration": 150,
  "rating": 9.0
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Movie updated successfully",
  "data": "Movie attributes updated successfully"
}
```

### Get All Movies
```http
GET /api/v1/movies
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "movieId": 1,
      "movieName": "Inception",
      "duration": 148,
      "genre": "SCIFI",
      "releaseDate": "2010-07-16",
      "rating": 8.8,
      "language": "ENGLISH"
    }
  ]
}
```

### Get Movie by ID
```http
GET /api/v1/movies/{movieId}
```

---

## 🏛️ Theater Management APIs

### Create Theater
```http
POST /api/v1/theaters
```

**Request Body:**
```json
{
  "name": "PVR Cinemas",
  "address": "Mumbai, Maharashtra",
  "noOfScreens": 5
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Theater added successfully",
  "data": "Theater has been saved with theaterId: 1"
}
```

### Add Theater Seats
```http
POST /api/v1/theaters/{theaterId}/seats
```

**Request Body:**
```json
{
  "theaterId": 1,
  "noOfClassicSeats": 50,
  "noOfPremiumSeats": 30
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Theater seats added successfully",
  "data": "Theater seats have been generated successfully"
}
```

### Get All Theaters
```http
GET /api/v1/theaters
```

### Get Theater by ID
```http
GET /api/v1/theaters/{theaterId}
```

---

## 🎭 Show Management APIs

### Create Show
```http
POST /api/v1/shows
```

**Request Body:**
```json
{
  "movieName": "Inception",
  "showDate": "2026-01-25",
  "showTime": "18:00:00",
  "theaterId": 1
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Show created successfully",
  "data": "Show has been saved to the database with showId: 1"
}
```

### Add Show Seats
```http
POST /api/v1/shows/{showId}/seats
```

**Request Body:**
```json
{
  "showId": 1,
  "priceOfClassicSeats": 200,
  "priceOfPremiumSeats": 400
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Show seats added successfully",
  "data": "Show seats have been generated successfully for showId: 1"
}
```

### Get All Shows
```http
GET /api/v1/shows
```

### Get Show by ID
```http
GET /api/v1/shows/{showId}
```

---

## 💺 Seat Management APIs

### Get Available Seats
```http
POST /api/v1/seats/available
```

**Request Body:**
```json
{
  "movieName": "Inception",
  "showDate": "2026-01-25",
  "showTime": "18:00:00",
  "theaterId": 1
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "showId": 1,
    "movieName": "Inception",
    "theaterName": "PVR Cinemas",
    "showDate": "2026-01-25",
    "showTime": "18:00:00",
    "availableSeats": {
      "CLASSIC": ["A1", "A2", "A3", "B1", "B2"],
      "PREMIUM": ["C1", "C2", "C3"]
    },
    "pricing": {
      "CLASSIC": 200,
      "PREMIUM": 400
    },
    "totalAvailable": 8
  }
}
```

### Select Seats (Temporary Selection)
```http
POST /api/v1/seats/select
```

**Request Body:**
```json
{
  "showId": 1,
  "selectedSeats": ["A1", "A2"],
  "userMobNo": "9876543210"
}
```

**Validation Rules:**
- `showId`: Required, must be positive
- `selectedSeats`: Required, at least one seat
- `userMobNo`: Required, exactly 10 digits

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Seats selected successfully",
  "data": {
    "selectedSeats": ["A1", "A2"],
    "expiresAt": "2026-01-23T11:00:00",
    "totalAmount": 400
  }
}
```

**Note:** Selected seats are held for 10 minutes

### Release Seats
```http
POST /api/v1/seats/release
```

**Request Body:**
```json
{
  "showId": 1,
  "selectedSeats": ["A1", "A2"],
  "userMobNo": "9876543210"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Seats released successfully",
  "data": "Seats released successfully"
}
```

---

## 🎫 Ticket Management APIs

### Book Ticket
```http
POST /api/v1/tickets/book
```

**Request Body:**
```json
{
  "movieName": "Inception",
  "showDate": "2026-01-25",
  "showTime": "18:00:00",
  "requestedSeats": ["A1", "A2"],
  "theaterId": 1,
  "mobNo": "9876543210"
}
```

**Validation Rules:**
- `movieName`: Required
- `showDate`: Required, must be today or future
- `showTime`: Required
- `requestedSeats`: Required, at least one seat
- `theaterId`: Required, must be positive
- `mobNo`: Required, exactly 10 digits

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Ticket booked successfully",
  "data": {
    "ticketId": "550e8400-e29b-41d4-a716-446655440000",
    "movieName": "Inception",
    "showDate": "2026-01-25",
    "showTime": "18:00:00",
    "theaterNameAndAddress": "PVR Cinemas Mumbai, Maharashtra",
    "totalAmtPaid": 400,
    "ticketStatus": "CONFIRMED",
    "bookedAt": "2026-01-23T10:30:00"
  }
}
```

**Note:** You must select seats first using `/api/v1/seats/select`

### Get Ticket by ID
```http
GET /api/v1/tickets/{ticketId}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "ticketId": "550e8400-e29b-41d4-a716-446655440000",
    "movieName": "Inception",
    "showDate": "2026-01-25",
    "showTime": "18:00:00",
    "theaterNameAndAddress": "PVR Cinemas Mumbai",
    "totalAmtPaid": 400,
    "ticketStatus": "CONFIRMED",
    "refundStatus": "NOT_APPLICABLE"
  }
}
```

### Get User Tickets
```http
GET /api/v1/tickets/user/{userId}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "ticketId": "550e8400-e29b-41d4-a716-446655440000",
      "movieName": "Inception",
      "showDate": "2026-01-25",
      "totalAmtPaid": 400,
      "ticketStatus": "CONFIRMED"
    }
  ]
}
```

---

## ❌ Cancellation & Refund APIs

### Cancel Ticket
```http
POST /api/v1/tickets/{ticketId}/cancel
```

**Request Body:**
```json
{
  "cancellationReason": "Change of plans"
}
```

**Validation Rules:**
- `cancellationReason`: Optional, max 500 characters

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Ticket cancelled successfully",
  "data": {
    "ticketId": "550e8400-e29b-41d4-a716-446655440000",
    "cancellationSuccess": true,
    "refundAmount": 320,
    "refundPercentage": 80,
    "message": "Ticket cancelled successfully. Refund of ₹320 (80%) will be processed.",
    "processingTime": "3-5 business days"
  }
}
```

**Refund Policy:**
- **More than 48 hours before show:** 100% refund
- **24-48 hours before show:** 80% refund
- **12-24 hours before show:** 50% refund
- **Less than 12 hours before show:** No refund

### Get Refund Status
```http
GET /api/v1/tickets/{ticketId}/refund
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "refundId": 1,
    "ticketId": "550e8400-e29b-41d4-a716-446655440000",
    "refundAmount": 320,
    "refundPercentage": 80,
    "refundStatus": "PROCESSED",
    "refundMethod": "Original Payment Method",
    "processedAt": "2026-01-23T10:35:00"
  }
}
```

---

## 📋 Waitlist Management APIs

### Add to Waitlist
```http
POST /api/v1/waitlist
```

**Request Body:**
```json
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

**Validation Rules:**
- `mobNo`: Required, exactly 10 digits
- `theaterId`: Required, must be positive
- `movieName`: Required
- `showDate`: Required, must be today or future
- `showTime`: Required
- `requestedSeatType`: Required, must be "CLASSIC" or "PREMIUM"
- `numberOfSeats`: Required, 1-10 seats

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Added to waitlist successfully",
  "data": {
    "waitlistId": 1,
    "position": 3,
    "movieName": "Inception",
    "showDate": "2026-01-25",
    "showTime": "18:00:00",
    "requestedSeatType": "PREMIUM",
    "numberOfSeats": 2,
    "status": "WAITING",
    "estimatedWaitTime": "15 minutes"
  }
}
```

### Cancel Waitlist Entry
```http
DELETE /api/v1/waitlist/{waitlistId}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Waitlist entry cancelled successfully"
}
```

### Get User Waitlists
```http
GET /api/v1/waitlist/user/{userId}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "waitlistId": 1,
      "position": 3,
      "movieName": "Inception",
      "showDate": "2026-01-25",
      "status": "WAITING",
      "numberOfSeats": 2
    }
  ]
}
```

---

## 💰 Dynamic Pricing APIs

### Get Show Pricing
```http
GET /api/v1/pricing/shows/{showId}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "showId": 1,
    "movieName": "Inception",
    "showDate": "2026-01-25",
    "showTime": "18:00:00",
    "basePricing": {
      "CLASSIC": 200,
      "PREMIUM": 400
    },
    "currentPricing": {
      "CLASSIC": 220,
      "PREMIUM": 440
    },
    "appliedFactors": [
      {
        "type": "DEMAND_BASED",
        "multiplier": 1.1,
        "reason": "High demand - 80% seats booked"
      }
    ],
    "totalPriceIncrease": 10
  }
}
```

### Apply Dynamic Pricing
```http
POST /api/v1/pricing/shows/{showId}/apply
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Dynamic pricing applied successfully"
}
```

### Get All Pricing Configs
```http
GET /api/v1/pricing/configs
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "configId": 1,
      "factorType": "DEMAND_BASED",
      "threshold": 80,
      "priceMultiplier": 1.1,
      "isActive": true,
      "description": "10% increase when 80% seats booked"
    }
  ]
}
```

### Get Active Pricing Configs
```http
GET /api/v1/pricing/configs/active
```

### Update Pricing Config
```http
PUT /api/v1/pricing/configs/{configId}
```

**Request Body:**
```json
{
  "threshold": 85,
  "priceMultiplier": 1.15,
  "isActive": true
}
```

---

## ⚠️ Error Responses

All errors follow a consistent format:

**404 Not Found:**
```json
{
  "timestamp": "2026-01-23T10:30:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "User not found with userId: '123'",
  "path": "/api/v1/users/123"
}
```

**400 Bad Request:**
```json
{
  "timestamp": "2026-01-23T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Request validation failed",
  "path": "/api/v1/users",
  "details": [
    "Mobile number must be 10 digits",
    "Email should be valid"
  ]
}
```

**409 Conflict:**
```json
{
  "timestamp": "2026-01-23T10:30:00",
  "status": 409,
  "error": "Seat Unavailable",
  "message": "The requested seats are unavailable",
  "path": "/api/v1/tickets/book"
}
```

**500 Internal Server Error:**
```json
{
  "timestamp": "2026-01-23T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred. Please try again later.",
  "path": "/api/v1/tickets/book"
}
```

---

## 📊 HTTP Status Codes Used

| Status Code | Meaning | When Used |
|-------------|---------|-----------|
| 200 OK | Success | GET, PUT, DELETE operations |
| 201 Created | Resource created | POST operations |
| 400 Bad Request | Invalid input | Validation errors |
| 404 Not Found | Resource not found | Invalid IDs |
| 409 Conflict | Business rule violation | Seat unavailable |
| 500 Internal Server Error | Server error | Unexpected errors |

---

## 🔄 Typical Booking Flow

1. **Get Available Seats**
   ```
   POST /api/v1/seats/available
   ```

2. **Select Seats (Temporary Hold)**
   ```
   POST /api/v1/seats/select
   ```

3. **Book Ticket**
   ```
   POST /api/v1/tickets/book
   ```

4. **Get Ticket Details**
   ```
   GET /api/v1/tickets/{ticketId}
   ```

5. **Cancel Ticket (if needed)**
   ```
   POST /api/v1/tickets/{ticketId}/cancel
   ```

---

## 📱 Testing with Swagger

Access the interactive API documentation:
```
http://localhost:8080/swagger-ui.html
```

Features:
- Try all APIs directly from browser
- See request/response schemas
- View all available endpoints
- Test different scenarios

---

## 🔐 Future Enhancements

- JWT-based authentication
- Rate limiting
- Payment gateway integration
- Email/SMS notifications
- Real-time seat updates via WebSocket
- Admin dashboard
- Analytics and reporting

---

*API Documentation - CineConnect v1.0*
*Last Updated: January 23, 2026*
