# 🎬 CineConnect - Movie Ticket Booking System

A comprehensive movie ticket booking platform built with Spring Boot, featuring dynamic pricing, waitlist management, and smart cancellation policies.

## 🚀 Features

### Core Features
- 👤 **User Management** - Registration and profile management
- 🎬 **Movie Management** - Add and browse movies
- 🏛️ **Theater Management** - Manage theaters and seating
- 🎭 **Show Scheduling** - Create and manage movie shows
- 💺 **Smart Seat Selection** - Real-time availability with temporary holds
- 🎟️ **Ticket Booking** - Instant booking confirmation

### Advanced Features

#### 💰 Cancellation & Refund
- Time-based refund policy (100% to 0% based on time before show)
- Automatic refund processing
- Instant seat release back to inventory

**Refund Policy:**
- More than 48 hours: **100% refund**
- 24-48 hours: **75% refund**
- 12-24 hours: **50% refund**
- 6-12 hours: **25% refund**
- Less than 6 hours: **No refund**

#### 📈 Dynamic Pricing
- **Demand-Based:** Prices increase as seats fill up
- **Time-Based:** Different pricing for morning/evening shows
- **Day-Based:** Weekend vs weekday pricing
- Real-time price calculation

#### 📋 Waitlist System
- FIFO queue management
- Automatic notifications when seats become available
- Position tracking
- Auto-expiry after show time

## 🛠️ Technology Stack

- **Java 21** + **Spring Boot 3.2.5**
- **MySQL 8.0** + **JPA/Hibernate**
- **Maven** + **Lombok**
- **JUnit 5** + **Mockito**
- **Swagger/OpenAPI** for API documentation

## 📦 Prerequisites

- Java JDK 21+
- MySQL 8.0+
- Maven 3.6+ (or use included wrapper)
- IDE (IntelliJ IDEA / Eclipse)

## 🚀 Quick Start

### 1. Setup Database

```sql
CREATE DATABASE cineconnect;
```

### 2. Configure Application

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cineconnect
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Run Application

**Using IDE:**
- Open project in IntelliJ/Eclipse
- Run `BookMyShowApplication.java`

**Using Maven:**
```bash
mvn spring-boot:run
```

**Using JAR:**
```bash
mvn clean package
java -jar target/book-my-show-application-0.0.1-SNAPSHOT.war
```

### 4. Access Application

- **Application:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html

## 📡 API Endpoints

**Base URL:** `http://localhost:8080/api/v1`

### User Management
```
POST   /api/v1/users                      - Register user
GET    /api/v1/users/{userId}             - Get user
GET    /api/v1/users/mobile/{mobNo}       - Get user by mobile
```

### Movie Management
```
POST   /api/v1/movies                     - Add movie
GET    /api/v1/movies                     - Get all movies
GET    /api/v1/movies/{movieId}           - Get movie
PUT    /api/v1/movies/{movieId}           - Update movie
```

### Theater Management
```
POST   /api/v1/theaters                   - Add theater
POST   /api/v1/theaters/{id}/seats        - Add seats
GET    /api/v1/theaters                   - Get all theaters
```

### Show Management
```
POST   /api/v1/shows                      - Create show
POST   /api/v1/shows/{id}/seats           - Add show seats
GET    /api/v1/shows                      - Get all shows
```

### Booking Flow
```
POST   /api/v1/seats/available            - Check availability
POST   /api/v1/seats/select               - Select seats (10 min hold)
POST   /api/v1/tickets/book               - Book ticket
GET    /api/v1/tickets/{ticketId}         - Get ticket
GET    /api/v1/tickets/user/{userId}      - Get user bookings
```

### Cancellation & Refund
```
POST   /api/v1/tickets/{id}/cancel        - Cancel ticket
GET    /api/v1/tickets/{id}/refund        - Get refund status
```

### Waitlist
```
POST   /api/v1/waitlist                   - Add to waitlist
DELETE /api/v1/waitlist/{id}              - Cancel waitlist
GET    /api/v1/waitlist/user/{userId}     - Get user waitlists
```

### Dynamic Pricing
```
GET    /api/v1/pricing/shows/{id}         - Get pricing
POST   /api/v1/pricing/shows/{id}/apply   - Apply pricing
GET    /api/v1/pricing/configs            - Get pricing rules
```

**Total: 31 RESTful APIs**

## 💡 Usage Example

### Complete Booking Flow

1. **Register User**
```bash
POST /api/v1/users
{
  "name": "John Doe",
  "emailId": "john@example.com",
  "mobNo": "9876543210"
}
```

2. **Check Available Seats**
```bash
POST /api/v1/seats/available
{
  "movieName": "Inception",
  "showDate": "2026-01-25",
  "showTime": "18:00:00",
  "theaterId": 1
}
```

3. **Select Seats**
```bash
POST /api/v1/seats/select
{
  "showId": 1,
  "selectedSeats": ["1A", "1B"],
  "userMobNo": "9876543210"
}
```

4. **Book Ticket**
```bash
POST /api/v1/tickets/book
{
  "movieName": "Inception",
  "showDate": "2026-01-25",
  "showTime": "18:00:00",
  "requestedSeats": ["1A", "1B"],
  "theaterId": 1,
  "mobNo": "9876543210"
}
```

5. **Cancel if Needed**
```bash
POST /api/v1/tickets/{ticketId}/cancel
{
  "cancellationReason": "Emergency"
}
```

## 📁 Project Structure

```
CineConnect/
├── Controllers/        # 9 REST Controllers
├── Service/           # 10 Business Logic Services
├── Repository/        # 11 Data Access Repositories
├── Models/            # 11 JPA Entities
├── Requests/          # 10 Request DTOs
├── Responses/         # 5 Response DTOs
├── Exceptions/        # 6 Custom Exceptions
└── Enums/             # 7 Enumerations
```

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=CancellationServiceTest
```

**Test Coverage:** 74 test cases
- CancellationService: 16 tests
- DynamicPricingService: 18 tests
- WaitlistService: 18 tests
- TicketService: 10 tests
- Integration Tests: 12 tests

## 🗄️ Database

**Tables:** 11 tables including users, movies, theaters, shows, tickets, waitlists, pricing_configs, refund_transactions

**Auto-created by Hibernate** with `spring.jpa.hibernate.ddl-auto=update`

## 📊 Response Format

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
  "error": "Not Found",
  "message": "User not found",
  "path": "/api/v1/users/123"
}
```

## 🎯 Key Business Rules

- Seats held for **10 minutes** during selection
- **FIFO** waitlist queue management
- **Time-based** refund calculation
- **Multi-factor** dynamic pricing
- Automatic seat cleanup for expired selections

## 🎨 Supported Options

**Genres:** `HORROR`, `ROMANTIC`, `COMEDY`, `BIOGRAPHY`, `DRAMA`, `ACTION`, `THRILLER`, `SCIFI`, `FICTIONAL`

**Languages:** `ENGLISH`, `HINDI`, `TELUGU`, `TAMIL`, `KANNADA`, `BENGALI`, `MARATHI`, `PUNJABI`

**Seat Types:** `CLASSIC`, `PREMIUM`

## 📈 Project Stats

- **API Endpoints:** 31
- **Controllers:** 9
- **Services:** 10
- **Test Cases:** 74
- **Code Quality:** Principal Software Engineer standards ⭐

## 🔧 Configuration Tips

- Use `spring.jpa.show-sql=true` for SQL debugging
- Configure email for waitlist notifications
- Set up scheduled tasks for cleanup
- Enable Swagger at `/swagger-ui.html`

## 📚 Additional Documentation

- `API_DOCUMENTATION.md` - Detailed API reference
- `QUICK_START.md` - 5-minute setup guide
- `REFACTORING_COMPLETE.md` - Recent improvements

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/NewFeature`)
3. Commit changes (`git commit -m 'Add NewFeature'`)
4. Push to branch (`git push origin feature/NewFeature`)
5. Open Pull Request

## 📄 License

MIT License - see LICENSE file for details

---

**Built with ❤️ using Spring Boot 3.2.5**

*⭐ Star this repo if you find it useful!*
