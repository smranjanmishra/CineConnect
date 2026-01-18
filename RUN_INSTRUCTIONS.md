# CineConnect - Run Instructions

## Prerequisites
- Java 21 installed
- MySQL database running on localhost:3306
- Database name: `cinemaDB`
- MySQL credentials: username=`root`, password=`admin`

## How to Run the Application

### Option 1: Using IDE (Recommended)
1. Open the project in IntelliJ IDEA or Eclipse
2. Right-click on `BookMyShowApplication.java`
3. Select "Run" or "Debug"
4. The application will start on port 8080

### Option 2: Using Maven (if Maven is installed)
```bash
mvn clean install
mvn spring-boot:run
```

### Option 3: Using Maven Wrapper (if wrapper is configured)
```bash
./mvnw clean install
./mvnw spring-boot:run
```

## Accessing the Application

Once the application is running:

### Swagger UI (API Documentation)
- URL: http://localhost:8080/swagger-ui/index.html
- This provides interactive API documentation for all endpoints

### New Feature Endpoints

#### 1. Cancellation & Refund APIs
- **POST** `/api/cancellation/cancel` - Cancel a ticket
  - Request Body:
    ```json
    {
      "ticketId": "ticket-uuid",
      "cancellationReason": "reason"
    }
    ```
  
- **GET** `/api/cancellation/refund-status/{ticketId}` - Get refund status

#### 2. Waitlist Management APIs
- **POST** `/api/waitlist/add` - Add to waitlist
  - Request Body:
    ```json
    {
      "mobNo": "1234567890",
      "theaterId": 1,
      "movieName": "Movie Name",
      "showDate": "2026-01-20",
      "showTime": "18:00:00",
      "requestedSeatType": "PREMIUM",
      "numberOfSeats": 2
    }
    ```

- **DELETE** `/api/waitlist/cancel/{waitlistId}` - Cancel waitlist entry
- **GET** `/api/waitlist/user/{userId}` - Get user's waitlist entries

#### 3. Dynamic Pricing APIs
- **GET** `/api/pricing/show/{showId}` - Get dynamic pricing for a show
- **POST** `/api/pricing/apply/{showId}` - Apply dynamic pricing to a show
- **GET** `/api/pricing/configs` - Get all pricing configurations
- **GET** `/api/pricing/configs/active` - Get active pricing configurations
- **PUT** `/api/pricing/configs/{configId}` - Update pricing configuration

## Testing the Features

### 1. Testing Cancellation & Refund
1. First book a ticket using the existing `/ticket/bookTicket` endpoint
2. Note the `ticketId` from the response
3. Use `/api/cancellation/cancel` to cancel the ticket
4. Check refund status using `/api/cancellation/refund-status/{ticketId}`

**Refund Policy:**
- More than 48 hours before show: 100% refund
- 24-48 hours: 75% refund
- 12-24 hours: 50% refund
- 6-12 hours: 25% refund
- Less than 6 hours: No refund

### 2. Testing Dynamic Pricing
1. Create a show using existing endpoints
2. Get pricing for the show: `/api/pricing/show/{showId}`
3. Book some tickets to increase occupancy
4. Check pricing again - it should be higher due to increased demand
5. Apply dynamic pricing: `/api/pricing/apply/{showId}`

**Pricing Factors:**
- **Demand-based**: Higher prices when >70% seats booked
- **Time-based**: Morning shows cheaper, evening shows premium
- **Day-based**: Weekend premium pricing

### 3. Testing Waitlist
1. Try to book tickets when show is full
2. Add user to waitlist: `/api/waitlist/add`
3. When someone cancels, waitlisted users are automatically notified
4. Check user's waitlist: `/api/waitlist/user/{userId}`

## Running Tests

To run the comprehensive test suite:

```bash
mvn test
```

Or in IDE:
- Right-click on `src/test/java` folder
- Select "Run All Tests"

## Database Schema

The following new tables will be created automatically:
- `pricing_configs` - Dynamic pricing rules
- `waitlists` - Waitlist entries
- `refund_transactions` - Refund tracking

Existing tables updated:
- `tickets` - Added cancellation and refund fields

## Troubleshooting

### MySQL Connection Issues
- Ensure MySQL is running
- Check database credentials in `application.properties`
- Create database manually if needed: `CREATE DATABASE cinemaDB;`

### Port Already in Use
- Change port in `application.properties`: `server.port=8081`

### Java Version Issues
- Ensure Java 21 is installed and JAVA_HOME is set correctly
- Check with: `java -version`

## Feature Highlights

### 1. Cancellation & Refund System
- Time-based refund policy
- Automatic refund processing
- Refund transaction tracking
- Seats released back to inventory
- Waitlist notification on cancellation

### 2. Dynamic Pricing
- Real-time price calculation
- Multiple pricing factors (demand, time, day)
- Configurable pricing rules
- Transparent pricing justification
- Automatic price updates

### 3. Waitlist System
- FIFO queue management
- Automatic notification when seats available
- Position tracking in queue
- Auto-expiry of old entries
- Integration with cancellation system

## API Testing with Postman

Import the following base URL: `http://localhost:8080`

Sample requests are available in the Swagger UI documentation.
