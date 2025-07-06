# BookMyShow Application

A comprehensive movie booking system built with Spring Boot that allows users to browse movies, book tickets, and manage theater operations.

## 🎬 Features

- **Movie Management**: Add new movies and update movie attributes (rating, duration)
- **Theater Management**: Add theaters and configure theater seating arrangements
- **Show Management**: Create shows and add show-specific seating
- **Ticket Booking**: Complete ticket booking system with error handling
- **User Management**: User registration and management
- **Seat Management**: Dynamic seat allocation for theaters and shows

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.2.5
- **Language**: Java 21
- **Database**: MySQL
- **Build Tool**: Maven
- **Architecture**: RESTful API
- **API Documentation**: SpringDoc OpenAPI (Swagger)

### Dependencies

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Mail
- MySQL Connector
- Lombok
- SpringDoc OpenAPI UI

## 📋 Prerequisites

Before running this application, ensure you have:

- Java 21 or higher installed
- MySQL database server running
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/bookmyshow-application.git
cd bookmyshow-application
```

### 2. Database Configuration

Create a database named `cinemaDB` in your MySQL server.

The application is configured with the following database settings in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cinemaDB?createTableIfNotExist=true
spring.datasource.username=root
spring.datasource.password=admin
spring.jpa.hibernate.ddl-auto=update
```

**Note**: Update the username and password according to your MySQL configuration.

### 3. Build and Run

```bash
# Build the project using Maven wrapper
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access API Documentation

Once the application is running, you can access the Swagger UI at:
`http://localhost:8080/swagger-ui.html`

## 📚 API Endpoints

### Movie Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/movie/addMovie` | Add new movie |
| PUT | `/movie/updateMovieAttributes` | Update movie rating and duration |

### Theater Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/theater/addTheater` | Add new theater |
| POST | `/theater/addTheaterSeats` | Add seating arrangement to theater |

### Show Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/shows/addShow` | Create new show |
| POST | `/shows/addShowSeats` | Add seats to specific show |

### Ticket Booking

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/ticket/bookTicket` | Book tickets for a show |

### User Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/users/addUser` | Register new user |

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/acciojob/bookmyshowapplication/
│   │       ├── BookMyShowApplication.java
│   │       ├── ServletInitializer.java
│   │       ├── Controllers/
│   │       │   ├── MovieController.java
│   │       │   ├── ShowController.java
│   │       │   ├── TheaterController.java
│   │       │   ├── TicketController.java
│   │       │   └── UserController.java
│   │       ├── Service/
│   │       │   ├── MovieService.java
│   │       │   ├── ShowService.java
│   │       │   ├── TheaterService.java
│   │       │   ├── TicketService.java
│   │       │   └── UserService.java
│   │       ├── Models/
│   │       │   ├── Movie.java
│   │       │   ├── Show.java
│   │       │   ├── Theater.java
│   │       │   ├── TheaterSeat.java
│   │       │   ├── ShowSeat.java
│   │       │   ├── Ticket.java
│   │       │   └── User.java
│   │       ├── Repository/
│   │       ├── Enums/
│   │       └── Requests/
│   │           ├── AddShowRequest.java
│   │           ├── AddTheaterRequest.java
│   │           ├── BookTicketRequest.java
│   │           └── UpdateMovieRequest.java
│   └── resources/
│       ├── application.properties
│       └── static/
└── test/
```

## 🔧 Configuration

### Application Properties

Key configuration options in `application.properties`:

```properties
# Server Configuration
spring.application.name=book-my-show-application

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/cinemaDB?createTableIfNotExist=true
spring.datasource.username=root
spring.datasource.password=admin
spring.jpa.hibernate.ddl-auto=update

# API Documentation
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

## 🧪 Testing

Run tests using Maven wrapper:

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=MovieServiceTest
```

## 📦 Deployment

### WAR Deployment

The application is configured for WAR deployment with `ServletInitializer.java`. To deploy:

1. Build WAR file:
   ```bash
   ./mvnw clean package
   ```

2. Deploy the generated WAR file from `target/book-my-show-application-0.0.1-SNAPSHOT.war` to your application server (Tomcat, etc.)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request


**About**: Developing a feature-rich ticket booking platform akin to BookMyShow, offering seamless movie selection, secure transactions, and comprehensive admin theater management. 
