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

- **Backend**: Spring Boot 3.5.3+
- **Language**: Java 17+
- **Database**: MySQL/PostgreSQL (configurable)
- **Build Tool**: Maven
- **Architecture**: RESTful API
- **Dependencies**: 
  - Spring Web
  - Spring Data JPA
  - Lombok
  - Spring Boot Starter Web

## 📋 Prerequisites

Before running this application, make sure you have:

- Java 17 or higher installed
- Maven 3.6+ installed
- MySQL/PostgreSQL database server
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/bookmyshow-application.git
cd bookmyshow-application
```

### 2. Database Configuration

Create a database named `bookmyshow` in your MySQL/PostgreSQL server.

Update the `application.properties` file with your database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bookmyshow
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 3. Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

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
│   │       ├── Models/
│   │       └── Requests/
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
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/bookmyshow
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update

# Logging
logging.level.com.acciojob.bookmyshowapplication=DEBUG
```

## 🧪 Testing

Run tests using Maven:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MovieServiceTest
```

## 📦 Deployment

### WAR Deployment

The application is configured for WAR deployment with `ServletInitializer.java`. To deploy:

1. Build WAR file:
```bash
mvn clean package
```

2. Deploy the generated WAR file to your application server (Tomcat, etc.)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request
