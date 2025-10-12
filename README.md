<div align="center">

# 🚀 StockEasy - Stock Trading Simulator

 **A modern, feature-rich stock trading simulator built with Spring Boot**

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-orange.svg)](https://www.postgresql.org/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1.0-red.svg)](https://www.thymeleaf.org/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3.2-purple.svg)](https://getbootstrap.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**Empowering users to learn stock trading in a risk-free environment with real-time market simulation.**

</div>

---

## 📋 Table of Contents

- [✨ Features](#-features)
- [🏗️ Architecture](#-architecture)
- [🚀 Quick Start](#-quick-start)
- [🔧 Configuration](#-configuration)
- [📚 API Documentation](#-api-documentation)
- [🧪 Testing](#-testing)
- [🐳 Docker Support](#-docker-support)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)

---

## ✨ Features

### 🏦 Core Trading Features
- **📈 Real-time Stock Tracking** - Monitor stock prices with market data
- **💼 Portfolio Management** - Track holdings, performance, and profit/loss
- **📊 Transaction History** - Complete buy/sell transaction logging
- **🔍 Watchlists** - Create and monitor custom stock watchlists
- **👤 User Management** - Secure authentication and user profiles

### 🛡️ Security & Reliability
- **🔐 Spring Security** - JWT-based authentication and authorization
- **🛡️ Input Validation** - Comprehensive validation using Bean Validation
- **📝 Actuator Endpoints** - Health monitoring and metrics
- **🧼 Code Quality** - Spotless code formatting with Google Java Style

### 🎨 User Experience
- **🎯 Responsive UI** - Bootstrap-powered responsive web interface
- **📱 Mobile-Friendly** - Optimized for all device sizes
- **🔄 Real-time Updates** - Dynamic price updates and portfolio calculations
- **🎨 Modern Design** - Clean, intuitive user interface

---

## 🏗️ Architecture

### 📁 Project Structure
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

### 🧩 Technology Stack
| Component | Technology | Purpose |
|-----------|------------|---------|
| **Backend** | Spring Boot 3.3.0 | Application framework |
| **Database** | PostgreSQL | Primary data storage |
| **ORM** | Spring Data JPA | Database persistence |
| **Security** | Spring Security | Authentication & authorization |
| **Frontend** | Thymeleaf + Bootstrap | Server-side rendering |
| **API Docs** | SpringDoc OpenAPI | REST API documentation |
| **Testing** | JUnit 5 + H2 | Unit & integration tests |
| **Build** | Maven 3.9+ | Project management |

---

## 🚀 Quick Start

### 📋 Prerequisites
- **Java 21** (JDK)
- **Maven 3.9+**
- **PostgreSQL 14+** (for production)
- **Git** (for cloning)

### 🔧 Setup Instructions

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

## 🔧 Configuration

### 🌍 Environment Profiles

The application supports multiple Spring profiles:

| Profile | Description | Usage |
|---------|-------------|-------|
| **dev** | Development configuration | `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` |
| **test** | Testing configuration | `./mvnw test -Ptest` |
| **prod** | Production configuration | Default profile |

### ⚙️ Database Configuration

```yaml
# src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/stockeasy
    username: ${DB_USERNAME:stockeasy}
    password: ${DB_PASSWORD:password}
    driver-class-name: org.postgresql.Driver
```

### 🔐 Security Configuration

```yaml
# JWT and security settings
spring:
  security:
    jwt:
      secret: ${JWT_SECRET:your-secret-key}
      expiration: 86400000 # 24 hours
```

---

## 📚 API Documentation

### 📖 Swagger UI
Access interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### 🔗 Key API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/auth/login` | POST | User authentication |
| `/api/users` | POST | Create new user |
| `/api/stocks` | GET | Get all stocks |
| `/api/stocks/{symbol}` | GET | Get specific stock |
| `/api/portfolio` | GET | User portfolio |
| `/api/portfolio/buy` | POST | Buy stock |
| `/api/portfolio/sell` | POST | Sell stock |
| `/api/watchlist` | GET/POST | Watchlist management |

### 📋 Example API Request

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
     -H "Content-Type: application/json" \
     -d '{"username":"user@example.com", "password":"password"}'
```

---

## 🧪 Testing

### 🔄 Running Tests
```bash
# Run all tests
./mvnw test

# Run tests with specific profile
./mvnw test -Ptest

# Run integration tests
./mvnw verify
```

### 📊 Test Coverage
- **Unit Tests**: JUnit 5 + Mockito
- **Integration Tests**: Spring Boot Test
- **Security Tests**: Spring Security Test
- **Database Tests**: H2 in-memory database

---

## 🐳 Docker Support

### 📦 Build Docker Image
```bash
docker build -t stock-easy:latest .
```

### 🚀 Run Docker Container
```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=host.docker.internal \
  stock-easy:latest
```

### 🐳 Docker Compose
```yaml
# docker-compose.yml
version: '3.8'
services:
  stockeasy:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=postgres
    depends_on:
      - postgres
  
  postgres:
    image: postgres:14
    environment:
      - POSTGRES_DB=stockeasy
      - POSTGRES_USER=stockeasy
      - POSTGRES_PASSWORD=password
    ports:
      - "5432:5432"
```

```bash
# Start with Docker Compose
docker-compose up -d
```

---

## 🤝 Contributing

We welcome contributions! Here's how you can help:

### 🚀 Development Workflow

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Make your changes**
4. **Run tests and ensure they pass**
   ```bash
   ./mvnw clean verify
   ```
5. **Commit your changes**
   ```bash
   git commit -m 'feat: Add amazing feature'
   ```
6. **Push to your branch**
   ```bash
   git push origin feature/amazing-feature
   ```
7. **Open a Pull Request**

### 📝 Code Style

- **Java Code**: Google Java Format (enforced by Spotless)
- **Commit Messages**: Conventional Commits
- **Documentation**: JSDoc for public APIs

### 🧪 Pull Request Checklist

- [ ] Tests are added/updated
- [ ] Code follows style guidelines
- [ ] Documentation is updated
- [ ] CI/CD pipeline passes
- [ ] Reviewer approval obtained

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

### 📋 License Summary

- **Commercial use**: ✅ Permitted
- **Modification**: ✅ Permitted
- **Distribution**: ✅ Permitted
- **Private use**: ✅ Permitted
- **License and copyright notice**: Must be included

---

## 🌟 Acknowledgments

- **Spring Team** - For the amazing Spring Boot framework
- **Bootstrap Team** - For the responsive UI framework
- **PostgreSQL Team** - For the reliable database system
- **Thymeleaf Team** - For the elegant templating engine

---

<div align="center">

**Made with ❤️ by the StockEasy Team**

[![GitHub Stars](https://img.shields.io/github/stars/EECS3311F25/StockEasy?style=social)](https://github.com/EECS3311F25/StockEasy)
[![GitHub Forks](https://img.shields.io/github/forks/EECS3311F25/StockEasy?style=social)](https://github.com/EECS3311F25/StockEasy)
[![GitHub Issues](https://img.shields.io/github/issues/EECS3311F25/StockEasy)](https://github.com/EECS3311F25/StockEasy/issues)

[🔝 Back to top](#-stockeasy---stock-trading-simulator)

</div>
