# 🚀 CineConnect - Quick Start Guide

## 🎯 What Changed?

Your project has been **completely refactored** to Principal Software Engineer standards:
- ✅ **RESTful API endpoints** (no more camelCase or underscores)
- ✅ **Proper error handling** (custom exceptions + global handler)
- ✅ **Input validation** (all requests validated)
- ✅ **Professional logging** (SLF4J throughout)
- ✅ **Standardized responses** (consistent format)
- ✅ **Complete documentation** (API docs + Swagger)

---

## ⚡ Quick Start (3 Steps)

### 1. Configure Database
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cineconnect
spring.datasource.username=root
spring.datasource.password=your_password
```

### 2. Run Application
**Option A - IntelliJ IDEA:**
- Open project
- Run `BookMyShowApplication.java`

**Option B - Eclipse:**
- Import as Maven project
- Run `BookMyShowApplication.java`

### 3. Test APIs
Open browser: `http://localhost:8080/swagger-ui.html`

---

## 📍 New API Endpoints

### Core Endpoints (Most Used)

| Action | Endpoint | Method |
|--------|----------|--------|
| **Create User** | `/api/v1/users` | POST |
| **Add Movie** | `/api/v1/movies` | POST |
| **Add Theater** | `/api/v1/theaters` | POST |
| **Create Show** | `/api/v1/shows` | POST |
| **Add Show Seats** | `/api/v1/shows/{id}/seats` | POST |
| **Get Available Seats** | `/api/v1/seats/available` | POST |
| **Select Seats** | `/api/v1/seats/select` | POST |
| **Book Ticket** | `/api/v1/tickets/book` | POST |
| **Cancel Ticket** | `/api/v1/tickets/{id}/cancel` | POST |
| **Add to Waitlist** | `/api/v1/waitlist` | POST |

---

## 🎫 Complete Booking Flow

### Step 1: Create User
```bash
POST /api/v1/users
{
  "name": "John Doe",
  "emailId": "john@example.com",
  "mobNo": "9876543210"
}
```

### Step 2: Add Movie
```bash
POST /api/v1/movies
{
  "movieName": "Inception",
  "duration": 148,
  "genre": "SCIFI",
  "releaseDate": "2010-07-16",
  "rating": 8.8,
  "language": "ENGLISH"
}
```

### Step 3: Add Theater
```bash
POST /api/v1/theaters
{
  "name": "PVR Cinemas",
  "address": "Mumbai",
  "noOfScreens": 5
}
```

### Step 4: Add Theater Seats
```bash
POST /api/v1/theaters/1/seats
{
  "theaterId": 1,
  "noOfClassicSeats": 50,
  "noOfPremiumSeats": 30
}
```

### Step 5: Create Show
```bash
POST /api/v1/shows
{
  "movieName": "Inception",
  "showDate": "2026-01-25",
  "showTime": "18:00:00",
  "theaterId": 1
}
```

### Step 6: Add Show Seats
```bash
POST /api/v1/shows/1/seats
{
  "showId": 1,
  "priceOfClassicSeats": 200,
  "priceOfPremiumSeats": 400
}
```

### Step 7: Get Available Seats
```bash
POST /api/v1/seats/available
{
  "movieName": "Inception",
  "showDate": "2026-01-25",
  "showTime": "18:00:00",
  "theaterId": 1
}
```

### Step 8: Select Seats
```bash
POST /api/v1/seats/select
{
  "showId": 1,
  "selectedSeats": ["A1", "A2"],
  "userMobNo": "9876543210"
}
```

### Step 9: Book Ticket
```bash
POST /api/v1/tickets/book
{
  "movieName": "Inception",
  "showDate": "2026-01-25",
  "showTime": "18:00:00",
  "requestedSeats": ["A1", "A2"],
  "theaterId": 1,
  "mobNo": "9876543210"
}
```

---

## 🔥 Key Features

### 1. Cancellation & Refund
```bash
POST /api/v1/tickets/{ticketId}/cancel
{
  "cancellationReason": "Change of plans"
}
```

**Refund Policy:**
- 48+ hours before: 100%
- 24-48 hours: 80%
- 12-24 hours: 50%
- < 12 hours: 0%

### 2. Waitlist
```bash
POST /api/v1/waitlist
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

### 3. Dynamic Pricing
```bash
GET /api/v1/pricing/shows/1
```

---

## 📄 Documentation Files

| File | Purpose |
|------|---------|
| **QUICK_START.md** | This file - Quick reference |
| **API_DOCUMENTATION.md** | Complete API reference with examples |
| **REFACTORING_COMPLETE.md** | Detailed before/after comparison |
| **FINAL_SUMMARY.md** | Comprehensive delivery summary |

---

## ⚠️ Important Notes

### Validation Rules
- **Mobile Number:** Exactly 10 digits
- **Email:** Valid email format
- **Seats:** At least 1, max 10
- **Dates:** Future or present for bookings
- **Rating:** 0-10 range

### Response Format
**Success:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

**Error:**
```json
{
  "timestamp": "2026-01-23T10:30:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "User not found",
  "path": "/api/v1/users/123"
}
```

---

## 🐛 Troubleshooting

### Application Won't Start
- Check MySQL is running
- Verify database credentials in `application.properties`
- Ensure Java 21 is installed

### Swagger UI Not Loading
- URL: `http://localhost:8080/swagger-ui.html`
- Check if application is running
- Try: `http://localhost:8080/swagger-ui/index.html`

### API Returns 404
- Verify you're using `/api/v1/` prefix
- Check endpoint name (no underscores)
- Ensure correct HTTP method

### Validation Errors
- Check request body matches examples
- Verify all required fields
- Mobile: 10 digits, Email: valid format

---

## 🎯 Quick Test

Copy-paste this into Swagger or Postman:

### Create User:
```json
POST http://localhost:8080/api/v1/users
Content-Type: application/json

{
  "name": "Test User",
  "emailId": "test@example.com",
  "mobNo": "1234567890"
}
```

### Expected Response:
```json
{
  "success": true,
  "message": "User created successfully",
  "data": "User has been saved to the database with userId: 1"
}
```

---

## 📚 Learn More

- **Full API Documentation:** See `API_DOCUMENTATION.md`
- **Refactoring Details:** See `REFACTORING_COMPLETE.md`
- **Complete Summary:** See `FINAL_SUMMARY.md`

---

## ✅ Quick Checklist

- [ ] Database configured
- [ ] Application running
- [ ] Swagger UI accessible
- [ ] Test user created
- [ ] Test movie added
- [ ] Test theater added
- [ ] Show created
- [ ] Seats available
- [ ] Ticket booked

---

## 🎉 You're All Set!

Your CineConnect application is ready to use with:
- ✅ Professional API endpoints
- ✅ Proper error handling
- ✅ Complete validation
- ✅ All features working

**Base URL:** `http://localhost:8080/api/v1`
**Swagger UI:** `http://localhost:8080/swagger-ui.html`

---

*Happy Coding! 🚀*
