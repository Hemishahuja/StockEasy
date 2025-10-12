# Implementation Plan

## [Overview]
StockEasy is a stock trading simulator that enables users to practice portfolio management in a risk-free virtual environment. The system will provide real-time/simulated stock data, trading capabilities, portfolio tracking, and user authentication.

This implementation transforms the existing Spring Boot boilerplate into a full-featured stock trading simulator with PostgreSQL database, Thymeleaf server-side rendering, and comprehensive trading functionality.

## [Types]

**Core Domain Entities:**
- **User**: id (UUID), username, email, password_hash, balance (DECIMAL), created_at, last_login
- **Stock**: id (UUID), symbol (VARCHAR), name (VARCHAR), current_price (DECIMAL), price_change (DECIMAL), change_percentage (DECIMAL), market_cap (DECIMAL), volume (BIGINT), last_updated (TIMESTAMP)
- **Portfolio**: id (UUID), user_id (FK), total_value (DECIMAL), profit_loss (DECIMAL), created_at, updated_at
- **PortfolioItem**: id (UUID), portfolio_id (FK), stock_id (FK), quantity (INTEGER), average_buy_price (DECIMAL), current_value (DECIMAL)
- **Transaction**: id (UUID), user_id (FK), stock_id (FK), type (ENUM: BUY/SELL), quantity (INTEGER), price (DECIMAL), total_amount (DECIMAL), timestamp, status (ENUM: PENDING/COMPLETED/FAILED)
- **Watchlist**: id (UUID), user_id (FK), stock_id (FK), added_at
- **MarketData**: id (UUID), stock_id (FK), timestamp, open_price, high_price, low_price, close_price, volume

**Validation Rules:**
- Stock symbol: 1-10 uppercase letters
- User balance: ≥ 0.00
- Transaction quantity: > 0
- Stock price: > 0.00
- User email: Valid email format
- Password: Minimum 8 characters, 1 uppercase, 1 lowercase, 1 number

**Relationships:**
- One User → One Portfolio
- One Portfolio → Many PortfolioItems
- One User → Many Transactions
- One User → Many Watchlist items
- One Stock → Many MarketData records
- One Stock → Many PortfolioItems
- One Stock → Many Transactions

## [Files]

**New Files to Create:**
- `src/main/java/com/example/stockeasy/domain/` (Domain entities)
  - `User.java`, `Stock.java`, `Portfolio.java`, `PortfolioItem.java`, `Transaction.java`, `Watchlist.java`, `MarketData.java`
- `src/main/java/com/example/stockeasy/repo/` (Repositories)
  - `UserRepository.java`, `StockRepository.java`, `PortfolioRepository.java`, `TransactionRepository.java`, `WatchlistRepository.java`, `MarketDataRepository.java`
- `src/main/java/com/example/stockeasy/service/` (Services)
  - `UserService.java`, `StockService.java`, `PortfolioService.java`, `TransactionService.java`, `MarketDataService.java`, `AuthenticationService.java`
- `src/main/java/com/example/stockeasy/web/` (Controllers)
  - `DashboardController.java`, `StockController.java`, `PortfolioController.java`, `TransactionController.java`, `WatchlistController.java`, `AuthController.java`
- `src/main/java/com/example/stockeasy/config/` (Configuration)
  - `SecurityConfig.java`, `DatabaseConfig.java`, `WebConfig.java`
- `src/main/resources/templates/` (Thymeleaf templates)
  - `dashboard.html`, `stocks.html`, `portfolio.html`, `transactions.html`, `watchlist.html`, `login.html`, `register.html`, `layout.html`
- `src/main/resources/static/` (Static assets)
  - `css/styles.css`, `js/app.js`, `images/`
- `src/main/resources/` (Configuration)
  - `data.sql` (Initial stock data), `schema.sql` (Database schema)

**Files to Modify:**
- `pom.xml` (Add dependencies)
- `src/main/resources/application.yml` (Database config)
- `src/main/resources/application-dev.yml` (Dev-specific config)
- `src/main/java/com/example/stockeasy/AppApplication.java` (Add annotations)
- `docker/Dockerfile` (Update for PostgreSQL)

**Files to Delete:**
- None (All existing files will be enhanced)

## [Functions]

**New Functions:**
- `UserService`: registerUser(), authenticateUser(), getUserBalance(), updateUserBalance()
- `StockService`: getStockBySymbol(), getAllStocks(), searchStocks(), updateStockPrices()
- `PortfolioService`: getPortfolioByUser(), calculatePortfolioValue(), getPortfolioPerformance()
- `TransactionService`: buyStock(), sellStock(), getTransactionHistory(), validateTransaction()
- `MarketDataService`: getStockHistory(), getRealTimeData(), simulateMarketMovement()
- `AuthenticationService`: login(), logout(), register(), validateSession()

**Modified Functions:**
- `HealthController`: Update health check to include database status
- `AppApplication`: Add @EnableJpaRepositories, @EntityScan, @EnableWebSecurity

## [Classes]

**New Classes:**
- **User**: Core user entity with authentication fields
- **Stock**: Stock information with real-time pricing
- **Portfolio**: User's stock holdings and performance tracking
- **Transaction**: Buy/sell operations with status tracking
- **Watchlist**: User's watched stocks
- **MarketData**: Historical price data for charts

**Service Classes:**
- **UserService**: User management and authentication
- **StockService**: Stock data retrieval and updates
- **PortfolioService**: Portfolio calculations and management
- **TransactionService**: Trading operations and validation
- **MarketDataService**: Historical data and real-time updates
- **AuthenticationService**: Security and session management

**Controller Classes:**
- **DashboardController**: Main dashboard with portfolio summary
- **StockController**: Stock listing, search, and details
- **PortfolioController**: Portfolio management and performance
- **TransactionController**: Buy/sell operations
- **WatchlistController**: Watchlist management
- **AuthController**: Login, registration, and logout

**Configuration Classes:**
- **SecurityConfig**: Spring Security configuration
- **DatabaseConfig**: JPA and PostgreSQL configuration
- **WebConfig**: Thymeleaf and web settings

## [Dependencies]

**New Dependencies (pom.xml):**
```xml
<!-- Database -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>bootstrap</artifactId>
    <version>5.3.0</version>
</dependency>
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>jquery</artifactId>
    <version>3.6.4</version>
</dependency>

<!-- Utilities -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

**Database:**
- PostgreSQL 15+ (Production-ready, full-featured)

## [Testing]

**Test Strategy:**
- **Unit Tests**: Service layer methods with Mockito
- **Integration Tests**: Repository layer with @DataJpaTest
- **Controller Tests**: MockMvc for web endpoints
- **Security Tests**: Authentication and authorization flows
- **End-to-End Tests**: Selenium for critical user journeys

**Test Files to Create:**
- `src/test/java/com/example/stockeasy/service/` (Service tests)
- `src/test/java/com/example/stockeasy/repo/` (Repository tests)
- `src/test/java/com/example/stockeasy/web/` (Controller tests)
- `src/test/java/com/example/stockeasy/security/` (Security tests)
- `src/test/resources/` (Test data and configurations)

## [Implementation Order]

1. **Database Setup** (2 days)
   - Configure PostgreSQL connection
   - Create database schema and initial data
   - Set up JPA entities and repositories

2. **Core Domain Layer** (3 days)
   - Implement domain entities with validation
   - Create repository interfaces
   - Set up database migrations

3. **Service Layer** (4 days)
   - Implement business logic services
   - Add authentication and security
   - Create market data simulation

4. **Web Layer** (5 days)
   - Build Thymeleaf templates
   - Implement controllers
   - Add form validation and error handling

5. **Security & Authentication** (2 days)
   - Configure Spring Security
   - Implement user registration/login
   - Add session management

6. **Frontend Styling** (2 days)
   - Add Bootstrap styling
   - Implement responsive design
   - Add interactive JavaScript

7. **Testing & Integration** (3 days)
   - Write unit and integration tests
   - Test critical user flows
   - Fix bugs and optimize performance

8. **Deployment Configuration** (1 day)
   - Update Docker configuration
   - Add production settings
   - Create deployment scripts

**Total Estimated Time**: 22 days

**Navigation Commands for Implementation:**
# Read Overview section
sed -n '/[Overview]/,/[Types]/p' implementation_plan.md | head -n 1 | cat

# Read Types section  
sed -n '/[Types]/,/[Files]/p' implementation_plan.md | head -n 1 | cat

# Read Files section
sed -n '/[Files]/,/[Functions]/p' implementation_plan.md | head -n 1 | cat

# Read Functions section
sed -n '/[Functions]/,/[Classes]/p' implementation_plan.md | head -n 1 | cat

# Read Classes section
sed -n '/[Classes]/,/[Dependencies]/p' implementation_plan.md | head -n 1 | cat

# Read Dependencies section
sed -n '/[Dependencies]/,/[Testing]/p' implementation_plan.md | head -n 1 | cat

# Read Testing section
sed -n '/[Testing]/,/[Implementation Order]/p' implementation_plan.md | head -n 1 | cat

# Read Implementation Order section
sed -n '/[Implementation Order]/,$p' implementation_plan.md | cat
