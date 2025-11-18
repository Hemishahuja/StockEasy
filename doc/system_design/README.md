# StockEasy System Design Documentation

This directory contains comprehensive system design documentation for the StockEasy stock trading simulator project.

## 📋 Documentation Overview

### 🎯 Core Design Documents

1. **[CRC Cards](CRC_Cards.md)** - Class Responsibility Collaboration cards for all system components
2. **[UML Class Diagram](UML_Class_Diagram.md)** - Complete class structure with relationships and inheritance
3. **[UML Sequence Diagrams](UML_Sequence_Diagrams.md)** - Dynamic behavior and interaction flows

### 🔧 Recent Updates

- **Portfolio Reset Functionality**: Added comprehensive reset sandbox feature
- **Price Auto-population Fix**: Fixed buy stock modal price display issues
- **Realistic Stock Prices**: Updated default stock prices to realistic market values

## 🏗️ System Architecture

### Layered Architecture
```
┌─────────────────────────────────────┐
│           Web Layer                 │  ← Controllers, REST APIs
├─────────────────────────────────────┤
│          Service Layer              │  ← Business Logic
├─────────────────────────────────────┤
│        Repository Layer             │  ← Data Access
├─────────────────────────────────────┤
│         Domain Layer                │  ← Business Entities
├─────────────────────────────────────┤
│           DTO Layer                 │  ← Data Transfer Objects
└─────────────────────────────────────┘
```

### Key Components

#### Domain Entities
- **User**: User authentication and profile management
- **Stock**: Stock information and market data
- **Portfolio**: User's stock holdings and investments
- **Transaction**: Buy/sell transaction history
- **Watchlist**: User's watched stocks

#### Service Layer
- **PortfolioService**: Portfolio management and trading
- **TransactionService**: Transaction processing
- **StockService**: Stock information and pricing
- **UserService**: User management and authentication
- **PortfolioResetService**: Portfolio reset functionality

#### Web Controllers
- **PortfolioController**: Portfolio dashboard and trading
- **AuthController**: User authentication
- **ResetController**: Portfolio reset operations
- **StockController**: Stock information display

## 🔄 Key Workflows

### 1. User Registration & Login
- User registration with validation
- Secure authentication with password hashing
- Session management and security

### 2. Stock Trading
- **Buy Process**: Price lookup → validation → portfolio update → transaction record
- **Sell Process**: Holdings check → validation → portfolio update → cash deposit

### 3. Portfolio Management
- Real-time portfolio value calculation
- Profit/loss tracking and analysis
- Transaction history management

### 4. Portfolio Reset (NEW)
- Complete portfolio reset with audit trail
- User confirmation and validation
- Transaction rollback protection
- Cash balance restoration to $100,000

## 🎨 UI Components

### Portfolio Dashboard
- **Quick Actions**: Buy/Sell/Analysis/History buttons
- **Reset Portfolio**: Prominent reset functionality with confirmation
- **Portfolio Holdings**: Real-time stock display with profit/loss
- **Responsive Design**: Bootstrap-based responsive interface

### Modals
- **Buy Stock Modal**: Stock lookup with auto-populated prices
- **Sell Stock Modal**: Holdings selection with validation
- **Reset Confirmation Modal**: Clear warnings and confirmation

## 🛠️ Technical Features

### Security
- **Spring Security**: Authentication and authorization
- **CSRF Protection**: Token-based CSRF protection
- **Password Encoding**: BCrypt password hashing
- **Input Validation**: Comprehensive input validation

### Data Management
- **JPA/Hibernate**: Object-relational mapping
- **Transaction Management**: ACID compliance with @Transactional
- **Audit Logging**: Portfolio reset event tracking
- **Database Relationships**: Proper entity relationships

### API Design
- **RESTful APIs**: Standard HTTP methods and status codes
- **DTO Pattern**: Separation of concerns with data transfer objects
- **Error Handling**: Comprehensive exception handling
- **JSON Serialization**: Proper API response formatting

## 📊 Stock Data

### Default Stock Portfolio
The system includes 8 realistic stocks with current market prices:

| Symbol | Company | Sector | Current Price |
|--------|---------|--------|---------------|
| AAPL | Apple Inc. | Technology | $185.75 |
| GOOGL | Alphabet Inc. | Technology | $148.30 |
| MSFT | Microsoft Corp. | Technology | $334.80 |
| AMZN | Amazon.com Inc. | Consumer Discretionary | $145.60 |
| TSLA | Tesla Inc. | Consumer Discretionary | $248.90 |
| NVDA | NVIDIA Corp. | Technology | $465.80 |
| META | Meta Platforms | Technology | $485.20 |
| NFLX | Netflix Inc. | Communication Services | $495.40 |

### Price Updates
- **Real-time Updates**: Background price fetching from external APIs
- **Portfolio Revaluation**: Automatic portfolio value recalculation
- **Historical Data**: Market data storage for analysis

## 🚀 Recent Enhancements

### Portfolio Reset Feature
- **Complete Reset**: Clear holdings, transactions, restore cash balance
- **Audit Trail**: Capture before-reset state for compliance
- **User Safety**: Multiple confirmation steps and warnings
- **Transaction Safety**: Atomic operations with rollback protection

### UI/UX Improvements
- **Price Auto-population**: Fixed buy modal price display
- **Realistic Prices**: Updated to current market values
- **Enhanced Feedback**: Better error messages and success notifications
- **Responsive Design**: Mobile-friendly interface

## 🔍 Design Patterns Used

1. **Repository Pattern**: Data access abstraction
2. **Service Layer Pattern**: Business logic centralization
3. **DTO Pattern**: API contract separation
4. **Factory Pattern**: Object creation management
5. **Observer Pattern**: Event-driven architecture
6. **Strategy Pattern**: Algorithm selection for pricing

## 📈 Performance Considerations

- **Database Optimization**: Proper indexing and query optimization
- **Caching Strategy**: Market data caching to reduce API calls
- **Lazy Loading**: Efficient relationship loading
- **Background Processing**: Async price updates to avoid blocking

## 🔧 Development Guidelines

### Code Organization
- **Package Structure**: Clear separation by functionality
- **Naming Conventions**: Consistent naming across all layers
- **Documentation**: Comprehensive JavaDoc and inline comments
- **Error Handling**: Consistent error handling patterns

### Testing Strategy
- **Unit Tests**: Service layer business logic testing
- **Integration Tests**: Controller and API testing
- **Mock Objects**: Repository testing with mocks
- **Test Data**: Realistic test data setup

## 🚦 Future Enhancements

### Potential Features
- **Real-time Charts**: Interactive stock price charts
- **Advanced Analytics**: Technical indicators and analysis
- **Portfolio Simulation**: What-if scenario analysis
- **Social Features**: Portfolio sharing and leaderboards
- **Mobile App**: Native mobile application
- **Advanced Trading**: Limit orders, stop losses

### Technical Improvements
- **Microservices**: Service decomposition for scalability
- **Event Sourcing**: Event-driven architecture
- **CQRS**: Command Query Responsibility Segregation
- **Cloud Deployment**: Containerization and orchestration

## 📞 Support & Maintenance

### Monitoring
- **Application Logs**: Structured logging for debugging
- **Performance Metrics**: Response time and throughput monitoring
- **Error Tracking**: Exception monitoring and alerting
- **User Analytics**: Usage pattern analysis

### Deployment
- **Spring Boot**: Embedded server for easy deployment
- **Database Migration**: Liquibase for schema management
- **Configuration Management**: Environment-specific configurations
- **Health Checks**: Application health monitoring

---

## 🎯 Quick Start

1. **Compile**: `mvn clean compile`
2. **Run**: `mvn spring-boot:run`
3. **Access**: Navigate to `http://localhost:8080`
4. **Login**: Use test credentials (username: `testuser`, password: `password`)

## 📝 Notes

- All prices are simulated for educational purposes
- The system is designed for learning stock trading concepts
- Portfolio reset functionality is intended for experimentation
- No real money is involved in this simulation

For detailed technical information, refer to the specific documentation files in this directory.
