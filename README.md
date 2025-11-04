<div align="center">

#  StockEasy - Stock Trading Simulator

 **A modern, feature-rich stock trading simulator built with Spring Boot**

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-orange.svg)](https://www.postgresql.org/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1.0-red.svg)](https://www.thymeleaf.org/)


**Empowering users to learn stock trading in a risk-free environment with real-time market simulation.**

</div>

---


## Features and motivation
StockEasy is a learning tool for practicing stock trading without real money. It simulates live-like prices, lets you buy/sell, track your portfolio, and review your decisions. The goal is to help students learn concepts such as orders, P/L, risk, and watchlists in a safe, classroom-friendly app. It’s built with Spring Boot so it’s easy to run locally and deploy for demos.

### Core Trading Features
- Real-time Stock Tracking - Monitor stock prices with market data
- Portfolio Management- Track holdings, performance, and profit/loss
- Transaction History- Complete buy/sell transaction logging
- Watchlists- Create and monitor custom stock watchlists
- User Management- Secure authentication and user profiles

### Security & Reliability
- Spring Security- Session-based authentication and authorization
- Input Validation- Comprehensive validation using Bean Validation
- Actuator Endpoints - Health monitoring and metrics
- Error Handling - Global exception handling with custom responses

### User Experience
- Responsive UI- Bootstrap-powered responsive web interface
- Mobile-Friendly - Optimized for all device sizes
- Real-time Updates - Dynamic price updates and portfolio calculations
- Modern Design - Clean, intuitive user interface

---

## Architecture

### Project Structure
```
src/
├── main/
│   ├── java/com/example/stockeasy/
│   │   ├── config/          # Spring configuration
│   │   ├── domain/          # JPA entities
│   │   ├── repo/            # Data repositories
│   │   ├── service/         # Business logic
│   │   └── web/             # Controllers & REST endpoints
│   └── resources/
│       ├── templates/       # Thymeleaf templates
│       └── application*.yml # Configuration files
└── test/
    └── java/com/example/stockeasy/
```

### Technology Stack
| Component | Technology | Purpose |
|-----------|------------|---------|
| **Backend** | Spring Boot 3.5.0 | Application framework |
| **Database** | H2/PostgreSQL | H2 for dev/test, PostgreSQL for production |
| **ORM** | Spring Data JPA | Database persistence |
| **Security** | Spring Security | Authentication & authorization |
| **Frontend** | Thymeleaf + Bootstrap | Server-side rendering |
| **API Docs** | SpringDoc OpenAPI | REST API documentation |
| **Testing** | JUnit 5 + H2 | Unit & integration tests |
| **Build** | Maven 3.9+ | Project management |

---

## Quick Start

### Prerequisites
- **Java 21** (JDK)
- **Maven 3.9+**
- **PostgreSQL 14+** (for production)
- **Git** (for cloning)

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/EECS3311F25/StockEasy.git
   cd StockEasy
   ```

2. **Configure PostgreSQL database**
   ```bash
   # Create database
   createdb stockeasy
   
   # Configure application.yml with your database credentials
   ```

3. **Build and run the application**
   ```bash
   # Build the project
   ./mvnw clean compile
   
   # Run the application
   ./mvnw spring-boot:run
   ```

4. **Access the application**
   - **Web Interface**: http://localhost:8080
   - **API Documentation**: http://localhost:8080/swagger-ui.html

---

## Configuration

### Environment Profiles

The application supports multiple Spring profiles:

| Profile | Description | Usage |
|---------|-------------|-------|
| **dev** | Development configuration | `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` |
| **test** | Testing configuration | `./mvnw test -Ptest` |
| **prod** | Production configuration | Default profile |

### Database Configuration

```yaml
# src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/stockeasy
    username: ${DB_USERNAME:stockeasy}
    password: ${DB_PASSWORD:password}
    driver-class-name: org.postgresql.Driver
```



---

## API Documentation

### Swagger UI
Access interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### Key API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/stocks` | GET | List all active stocks |
| `/stocks/search?symbol={symbol}` | GET | Search stock by symbol |
| `/stocks/{stockId}` | GET | Get stock details |
| `/stocks/sector/{sector}` | GET | Filter stocks by sector |
| `/stocks/industry/{industry}` | GET | Filter stocks by industry |
| `/stocks/above/{price}` | GET | Get stocks above price |
| `/stocks/below/{price}` | GET | Get stocks below price |
| `/stocks/api/refresh/{symbol}` | POST | Refresh market data |
| `/stocks/api/latest/{symbol}` | GET | Get latest market data |
| `/stocks/api/intraday/{symbol}` | GET | Get intraday data |
| `/portfolio/dashboard` | GET | User portfolio dashboard |
| `/portfolio/buy` | POST | Buy stocks |
| `/portfolio/sell` | POST | Sell stocks |
| `/portfolio/history` | GET | Transaction history |
| `/watchlist` | GET | User watchlist |
| `/watchlist/add` | POST | Add stock to watchlist |

### Example API Request

```bash
curl -X GET "http://localhost:8080/stocks/api/latest/AAPL?interval=5min" \
     -H "Accept: application/json"
```

---

## Testing

### Test Coverage
- **Unit Tests**: JUnit 5 + Mockito
- **Integration Tests**: Spring Boot Test
- **Security Tests**: Spring Security Test
- **Database Tests**: H2 in-memory database

---

## Contributing

We welcome small, focused PRs.

### How to contribute

Fork & branch: feat/<short-name> or fix/<short-name>.

Run tests: ./mvnw test (keep coverage for changed code).

Commit messages: short and actionable (e.g., feat: add buy/sell endpoints).

Open a PR describing:

What changed and why

How to test (steps or sample curl)

Any screenshots for UI changes

Issue labels

good first issue – starter tasks

bug – defects

enhancement – new features

documentation – README/docs fixes

## Code of Conduct

Be respectful and constructive. Assume good intent, keep reviews kind and specific.
<div align="center">



</div>
