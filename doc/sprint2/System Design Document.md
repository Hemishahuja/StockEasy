# 📌 System Design Document


**Course:** EECS3311 – SOFTWARE DESIGN  
**Team Name:** StockEasy 

**Team Members:**

| Name | Role |
|-------|------|
| Arushi | Backend |
| Divy   | Dashboard / UX handoff  |
| Hemish | Scrum Master / Developer  |
| Lama   | Scrum / Project tracking / Backend coordination  |
| Mark   | QA / Project Ops / Documentation |



---


# Table of Contents

1. CRC Cards
2. Environment and Assumptions
3. Architecture (Overview + Diagram)
4. System Decomposition (Class/Package mapping)
5. Error and Exception Strategy


# 1) CRC Cards :

| **Class Name:** User                                                     |
|--------------------------------------------------------------------------|
| **Parent Class :** Object                                                 |
| **Subclasses :** None                                                     |

| **Responsibilities:**                             | **Collaborators:**         |
|--------------------------------------------------|----------------------------|
| • Store user's profile and login credentials     | • Transaction              |
| • Link user to their transactions                | • UserService              |
| • Support authentication and session mapping     |                            | 

<br><br>

| **Class Name:** Stock |
|------------------------|
| **Parent Class :** Object |
| **Subclasses :** None |

| **Responsibilities:**                                  | **Collaborators:**   |
|--------------------------------------------------------|----------------------|
| • Represent tradable stock with a symbol and name      | • Transaction        |
| • Store current stock price                            | • StockService       |
| • Provide reference for buy/sell operations            |                      |

<br><br>

| **Class Name:** Transaction |
|-----------------------------|
| **Parent Class :** Object |
| **Subclasses :** BuyTransaction, SellTransaction |

| **Responsibilities:**                                  | **Collaborators:**         |
|--------------------------------------------------------|----------------------------|
| • Represent a single buy or sell operation             | • User                     |
| • Store quantity, stock, user, and price               | • Stock                    |
| • Provide data for transaction history                 | • TransactionService       |

<br><br>

| **Class Name:** BuyTransaction |
|-------------------------------|
| **Parent Class :** Transaction |
| **Subclasses :** None |

| **Responsibilities:**                           | **Collaborators:**         |
|--------------------------------------------------|----------------------------|
| • Represent a specific buy action                | • User                     |
| • Extend general transaction logic               | • Stock                    |


<br><br>

| **Class Name:** SellTransaction |
|--------------------------------|
| **Parent Class :** Transaction |
| **Subclasses :** None |

| **Responsibilities:**                           | **Collaborators:**         |
|--------------------------------------------------|----------------------------|
| • Represent a specific sell action               | • User                     |
| • Extend general transaction logic               | • Stock                    |

<br><br>

| **Class Name:** Watchlist |
|---------------------------|
| **Parent Class :** Object |
| **Subclasses :** None |

| **Responsibilities:**                              | **Collaborators:**         |
|----------------------------------------------------|----------------------------|
| • Represent a user-defined list of stocks to track | • User                     |
| • Store multiple stock symbols per user            | • Stock                    |
|                                                    | • WatchlistService         |

<br><br>

| **Class Name:** Portfolio |
|---------------------------|
| **Parent Class :** Object |
| **Subclasses :** None |

| **Responsibilities:**                                | **Collaborators:**         |
|------------------------------------------------------|----------------------------|
| • Represent user's holdings across multiple stocks   | • User                     |
| • Store current quantity, average cost, total value  | • PortfolioService         |

<br><br>

| **Class Name:** MarketData |
|----------------------------|
| **Parent Class :** Object |
| **Subclasses :** None |

| **Responsibilities:**                                 | **Collaborators:**         |
|-------------------------------------------------------|----------------------------|
| • Store price and metadata for a stock symbol         | • Stock                    |
| • Provide up-to-date price reference                  | • MarketDataService        |


<br><br>

| **Class Name:** UserService |
|-----------------------------|
| **Parent Class :** Object |
| **Subclasses :** None |

| **Responsibilities:**                                         | **Collaborators:**         |
|---------------------------------------------------------------|----------------------------|
| • Handle user registration and authentication                 | • User                     |
| • Integrate with Spring Security                              | • UserRepository           |
| • Provide user lookup for transactions                        | • UserDetailsService       |

<br><br>

| **Class Name:** TransactionService |
|------------------------------------|
| **Parent Class :** Object |
| **Subclasses :** None |

| **Responsibilities:**                                               | **Collaborators:**            |
|---------------------------------------------------------------------|-------------------------------|
| • Handle buy/sell logic                                             | • TransactionRepository       |
| • Validate stock and user data                                     | • UserService                 |
| • Save transaction records to the database                         | • StockService                |
|                                                                     | • Transaction, User, Stock    |

<br><br>

| **Class Name:** WatchlistService |
|----------------------------------|
| **Parent Class :** Object |
| **Subclasses :** None |

| **Responsibilities:**                                     | **Collaborators:**         |
|-----------------------------------------------------------|----------------------------|
| • Manage user's watchlist                                 | • WatchlistRepository      |
| • Add/remove stock symbols                                | • StockRepository          |
| • Fetch stocks on a user's watchlist                      | • UserService              |

<br><br>

| **Class Name:** PortfolioService |
|----------------------------------|
| **Parent Class :** Object |
| **Subclasses :** None |

| **Responsibilities:**                                             | **Collaborators:**         |
|-------------------------------------------------------------------|----------------------------|
| • Calculate user’s portfolio performance                         | • PortfolioRepository       |
| • Update holdings after transactions                             | • TransactionService        |
| • Calculate P/L, average cost, market value                      | • MarketDataService         |

<br><br>

| **Class Name:** MarketDataService |
|-----------------------------------|
| **Parent Class :** Object |
| **Subclasses :** None |

| **Responsibilities:**                                         | **Collaborators:**           |
|---------------------------------------------------------------|------------------------------|
| • Fetch real-time or simulated stock price                    | • MarketDataRepository       |
| • Interface with external API via AlphaVantageService         | • AlphaVantageService        |
| • Cache and update latest prices                              | • CacheService               |

<br><br>


<br><br>

| **Class Name:** StockService |
|------------------------------|
| **Parent Class :** Object |
| **Subclasses :** None |

| **Responsibilities:**                                     | **Collaborators:**   |
|-----------------------------------------------------------|----------------------|
| • Provide stock lookup and current price                  | • Stock              |
| • Handle stock creation and updates                       | • StockRepository    |
| • Support simulated or real-time pricing                  |                      |

<br><br>

| **Class Name:** WatchlistController |
|-------------------------------------|
| **Parent Class :** Object |
| **Subclasses :** None |

| **Responsibilities:**                                        | **Collaborators:**         |
|--------------------------------------------------------------|----------------------------|
| • Handle HTTP endpoints for adding/removing watchlist items | • WatchlistService         |
| • Display the user's watchlist                              | • UserService              |
| • Connect watchlist UI with backend                         | • StockService             |

<br><br> 

| **Class Name:** PortfolioController |
|-------------------------------------|
| **Parent Class :** Object |
| **Subclasses :** None |

| **Responsibilities:**                                        | **Collaborators:**         |
|--------------------------------------------------------------|----------------------------|
| • Handle HTTP requests for displaying portfolio              | • PortfolioService         |
| • Serve portfolio page with calculated metrics               | • MarketDataService        |

<br><br>
| **Class Name:** UserController |
|--------------------------------|
| **Parent Class (if any):** Object |
| **Subclasses (if any):** None |

| **Responsibilities:**                             | **Collaborators:**   |
|--------------------------------------------------|----------------------|
| • Handle login, logout, and user dashboard       | • UserService        |
| • Route HTTP requests related to users           |                      |

<br><br>

| **Class Name:** TransactionController |
|---------------------------------------|
| **Parent Class (if any):** Object |
| **Subclasses (if any):** None |

| **Responsibilities:**                                         | **Collaborators:**         |
|---------------------------------------------------------------|----------------------------|
| • Handle HTTP requests for buy/sell                           | • TransactionService       |
| • Receive form input from UI                                  | • UserService              |
| • Invoke service methods and redirect views                   | • StockService             |

<br><br>
| **Class Name:** DashboardController |
|-------------------------------------|
| **Parent Class :** Object |
| **Subclasses :** None |

| **Responsibilities:**                                   | **Collaborators:**     |
|---------------------------------------------------------|------------------------|
| • Route to user dashboard or homepage                   | • AuthService          |
| • Provide user-specific summaries or shortcuts          | • PortfolioService     |


<br><br>

| **Class Name:** HelpController |
|--------------------------------|
| **Parent Class :** Object |
| **Subclasses :** None |

| **Responsibilities:**                                   | **Collaborators:**     |
|---------------------------------------------------------|------------------------|
| • Display educational help content for stock trading    | • UserService          |
| • Serve help page with trading concepts and tips        |                        |
| • Provide beginner-friendly guidance                    |                        |

<br><br>

| **Class Name:** AuthController |
|-------------------------------|
| **Parent Class :** Object |
| **Subclasses :** None |

| **Responsibilities:**                              | **Collaborators:**     |
|----------------------------------------------------|------------------------|
| • Handle login/logout/register routes              | • AuthService          |
| • Map authentication endpoints                     | • SecurityConfig       |

<br><br>

| **Class Name:** AuthService |
|-----------------------------|
| **Parent Class :** Object |
| **Subclasses :** None |

| **Responsibilities:**                                   | **Collaborators:**         |
|---------------------------------------------------------|----------------------------|
| • Handle user authentication logic                      | • UserRepository           |
| • Manage sessions and credentials                       | • PasswordEncoderConfig    |

<br><br>
| **Class Name:** AlphaVantageService |
|-------------------------------------|
| **Parent Class :** Object |
| **Subclasses :** None |

| **Responsibilities:**                                     | **Collaborators:**             |
|-----------------------------------------------------------|--------------------------------|
| • Fetch live stock data via AlphaVantage API              | • AlphaVantageConfig           |
| • Parse and deliver time series price data                | • MarketDataService            |

<br><br>

| **Class Name:** CacheService |
|------------------------------|
| **Parent Class :** Object |
| **Subclasses :** None |

| **Responsibilities:**                                  | **Collaborators:**         |
|--------------------------------------------------------|----------------------------|
| • Cache market data or price lookups                   | • MarketDataService        |
| • Reduce external API calls                            |                            |

<br><br>

<br><br>

| **Class Name:** MarketDataRepository |
|--------------------------------------|
| **Parent Class :** JpaRepository<MarketData, Long> |
| **Subclasses :** None |

| **Responsibilities:**                                | **Collaborators:**       |
|------------------------------------------------------|--------------------------|
| • Provide access to market data storage              | • MarketDataService      |

<br><br>

| **Class Name:** PortfolioRepository |
|-------------------------------------|
| **Parent Class :** JpaRepository<Portfolio, Long> |
| **Subclasses :** None |

| **Responsibilities:**                                 | **Collaborators:**       |
|-------------------------------------------------------|--------------------------|
| • Provide access to user portfolio data               | • PortfolioService       |

<br><br>

| **Class Name:** WatchlistRepository |
|-------------------------------------|
| **Parent Class :** JpaRepository<Watchlist, Long> |
| **Subclasses :** None |

| **Responsibilities:**                                 | **Collaborators:**       |
|-------------------------------------------------------|--------------------------|
| • Handle persistence of user watchlists               | • WatchlistService       |


<br><br>
| **Class Name:** UserRepository |
|--------------------------------|
| **Parent Class (if any):** JpaRepository<User, Long> |
| **Subclasses (if any):** None |

| **Responsibilities:**                                 | **Collaborators:**   |
|--------------------------------------------------------|----------------------|
| • Provide database access to User entities            | • UserService        |
| • Find users by username or ID                        |                      |

<br><br>

| **Class Name:** TransactionRepository |
|---------------------------------------|
| **Parent Class (if any):** JpaRepository<Transaction, Long> |
| **Subclasses (if any):** None |

| **Responsibilities:**                                       | **Collaborators:**     |
|-------------------------------------------------------------|------------------------|
| • Save and retrieve Transaction entities                    | • TransactionService   |
| • Provide filtering by user or stock                        |                        |


<br><br>

| **Class Name:** StockRepository |
|---------------------------------|
| **Parent Class (if any):** JpaRepository<Stock, Long> |
| **Subclasses (if any):** None |

| **Responsibilities:**                              | **Collaborators:**   |
|-----------------------------------------------------|----------------------|
| • Provide database access to Stock entities         | • StockService       |
| • Lookup stocks by symbol                           |                      |


<br><br>


| **Class Name:** ValidationUtils |
|---------------------------------|
| **Parent Class :** Object |
| **Subclasses :** None |

| **Responsibilities:**                                          | **Collaborators:**     |
|----------------------------------------------------------------|------------------------|
| • Provide reusable input validation methods                    | • Used by controllers  |
| • Simplify form or DTO validation checks                       |                        |



---

# 2) Environment and Assumptions (Interaction with the environment)


## Runtime and Tooling
- **Java (JDK):** 21
- **Build:** Maven 3.9+
- **Spring Boot:** 3.3.0
- **Application Port:** 8080 (HTTP)

## Databases
- **Primary (dev):** PostgreSQL **14+** (local instance or container; final approach not determined)
    - **Connection:** Provided via `application-dev.properties` / environment variables
    - **Database name / credentials:** team-defined; do **not** commit secrets.
    - **Rationale:** Data (users, portfolios, transactions, watchlists, stocks/metadata, optional market data) should persist across app restarts.
- **Tests / quick local runs:** H2 (in-memory or file) under the `test` profile for fast feedback.
- **Schema migrations:** none yet (no Flyway/Liquibase in Sprint 1).

## Spring Profiles and Config
- **Profiles:**
    - `dev` → PostgreSQL 14+
    - `test` → H2
- **Activation:** `SPRING_PROFILES_ACTIVE=dev|test` or `-Dspring-boot.run.profiles=dev|test`.
- **Secrets/config:** supplied via environment variables or untracked local config; keep credentials out of VCS.

## OS and Tooling Assumptions
- **OS:** Windows/macOS/Linux (developer choice).
- **Containerization:** PostgreSQL may be run natively or via Docker (not determined); both approaches acceptable.
- **Frontend:** Server-rendered Thymeleaf with:
    - **Bootstrap 5.3.2** for responsive UI components
    - **Font Awesome 6.4.0** for icons
    - **JavaScript** for AJAX operations (watchlist, buy/sell, price refresh)
    - No Node/npm build process required

## Web and Network
- **Inbound:** HTTP on `localhost:8080`.
- **Outbound:** **Alpha Vantage API** at `api.alphavantage.co` for real-time market data
    - Refresh interval: configurable via `app.stock.market-data-refresh-interval`
    - Auto-refresh: implemented via `@Scheduled` tasks in `ScheduledTaskService`
    - Manual refresh: available via REST endpoints (`/stocks/api/refresh/{symbol}`)
    - Cache clearing: `/stocks/api/clear-cache/{symbol}`
- **REST API Endpoints:**
    - `/stocks/api/latest/{symbol}` - Get latest market data
    - `/stocks/api/intraday/{symbol}` - Get intraday price data
    - `/stocks/api/refresh/{symbol}` - Manually refresh market data
    - `/stocks/api/clear-cache/{symbol}` - Clear cached data for symbol
- **CORS/CSRF:** Spring Security defaults.

## Security
- **Auth:** Spring Security default login page; no custom configuration in Sprint 1.
- **Authorization:** default role model (if any) or open endpoints per controller mappings (to be hardened in later sprints).

# 3) Architecture

At a high level, the app uses a layered MVC design. Controllers translate HTTP requests into service calls and then return a view (Thymeleaf) or JSON. Services hold the business rules (buy/sell logic, portfolio valuation, watchlists) and act as the gateway to data. Repositories handle persistence via Spring Data JPA. Domain entities (User, Portfolio, Stock, Transaction, Watchlist, MarketData) define the key data structures that capture the core concepts of the application.
![Architecture diagram](/doc/sprint1/component.png)


# 4) System Decomposition

### Packages → Roles
- `com.example.stockeasy.web` → Controllers (serve views/JSON; thin)
- `com.example.stockeasy.service` → Business logic (`MarketDataService`, `PortfolioService`, `StockService`, `TransactionService`, `UserService`, `WatchlistService`)
- `com.example.stockeasy.repo` → Repositories (`*Repository`) with derived queries and targeted JPQL
- `com.example.stockeasy.domain` → Entities and relationships (`User`, `Stock`, `Portfolio`, `Transaction` (+ buy/sell), `Watchlist`, `MarketData`)  
### Notable mappings/examples
- `PortfolioController` → `PortfolioService` (+ `UserService`, `StockService`)
- `TransactionService` → `TransactionRepository`, `UserRepository`, `StockRepository`, `PortfolioRepository` (+ `PortfolioService`)

# 5) Known Issues and Sprint 2 Improvements

### Watchlist Persistence
- **Current State:** Watchlist uses an in-memory list in `WatchlistController` (resets on app restart)
- **Sprint 2 Goal:** Full database integration via `WatchlistRepository` and proper JPA entity linking between users and watched stocks
- **Impact:** Current watchlist is for demo/UI testing only; data does not persist

### Help Page User Context
- **Current State:** Help page displays "User" instead of actual user's first name
- **Sprint 2 Fix:** Inject `UserService` into `HelpController` and add authenticated user to model (following pattern from `DashboardController`, `StockController`, etc.)
- **Code Pattern Required:**
```java
@Autowired
private UserService userService;

@GetMapping("/help")
public String showHelpPage(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    User user = (User) userService.loadUserByUsername(username);
    model.addAttribute("user", user);
    return "help/index";
}
```

### Navigation Consistency
- **Current State:** Help page previously had different navbar structure (right-aligned, limited links)
- **Sprint 2 Status:** ✅ **RESOLVED** - Navigation standardized across all pages with full menu and user dropdown

### Search and Filtering Infrastructure
- **Current State:** Frontend placeholders exist for stock search/filtering
- **Sprint 2 Goal:** Implement backend infrastructure for stock search by symbol/name and filtering capabilities
- **Endpoints to Add:** Search/filter logic in `StockService` and `StockController`

### Help Page Content Expansion
- **Current State:** Basic educational content about stock trading concepts
- **Sprint 2 Goal:** 
    - Add beginner tips and outcome suggestions
    - Potentially integrate help tips into buy/sell workflow
    - Expand educational content based on user actions

---

# 6) Errors and Exception Strategy


We handle errors in three places. 
- First, in the controllers we validate inputs (Bean Validation) and show clear messages in the form or JSON. 
- Second, the services check business rules and throw small custom exceptions with messages the user can act on. 
- Lastly, if the database or the market-data service has issues, the app doesn’t crash. We either show cached data or a basic error page and keep things running. Our APIs return the same JSON fields for errors, we add a request ID to logs to trace problems, and any failed write is rolled back so we don’t leave half-saved data.

### Anticipated cases and response:

|Scenario|Where detected|Response to user|Retry / Fallback|
|---|---|---|---|
|Invalid input (missing/invalid fields)|Controller / Bean Validation|**400**; re-render form with field errors, or JSON `{status, error, message, fieldErrors}`|No retry; user fixes input|
|Authentication failure|Auth layer / UserService|**401**; redirect to login with message; JSON error envelope|No retry; optional lockout after repeats|
|Authorization denied|Security filter / Controller|**403**; access-denied page or JSON error|No retry|
|Resource not found (User/Stock/Portfolio/Watchlist)|Service / Repository|**404**; not-found page or JSON with hint|No retry|
|Business rule violation (e.g., selling more than owned, not enough funds)|Service|**409** or **422**; show actionable message (available qty/balance)|No retry; user adjusts request|
|Database error (connectivity/timeout, constraint)|Repository / JPA|**503** (connectivity) with `Retry-After`, or **409** (constraint)|Small automatic retry for timeouts; otherwise try again later|
|External market-data failure/timeout|MarketDataService|**200** with cached “last known” data and a “data delayed” banner; APIs set `stale=true`|Short retry with backoff; fall back to cache|

