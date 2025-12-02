# **StockEasy - System Design Document (Sprint 3)**

**Course:** EECS3311 – SOFTWARE DESIGN

**Team Name:** StockEasy

**Team Members:**

|Name|Role|
|---|---|
|Arushi|Backend|
|Divy|Dashboard / UX handoff|
|Hemish|Scrum Master / Developer|
|Lama|Scrum / Project tracking / Backend coordination|
|Mark|QA / Project Ops / Documentation|

---

# **Table of Contents**

1. Overview
    
2. Environment & System Assumptions
    
3. Software Architecture
    
    - 3.1 Architecture Diagram
        
    - 3.2 Architectural Rationale
        
    - 3.3 Sprint 3 Architectural Changes
        
4. System Decomposition
    
5. CRC Cards
    
6. Components & Responsibilities
    
    - 6.1 User & Auth Components
        
    - 6.2 Trading & Portfolio Components
        
    - 6.3 Background Services & External API
        
    - 6.4 Community & Educational Components
        
7. Key Workflows
    
8. Error and Exception Strategy
    
9. External Dependencies
    

---

# **1. Overview**

StockEasy is a Spring Boot–based stock trading simulator that follows the **MVC architecture pattern**.  
The system allows users to:

- Register and log in
    
- Trade stocks using simulated market data
    
- Manage a portfolio of holdings
    
- Track favorite stocks using a watchlist
    
- Reset their portfolio for experimentation
    
- Explore help, dashboard, community, and onboarding pages
    

All views are server-rendered with **Thymeleaf**.

## **Sprint 3 Additions**

Sprint 3 expanded StockEasy into a more community-driven educational platform with:

- **Community Hub**: Social media–style discussion forum for trading insights
    
- **Enhanced Stock Filtering**: Search and filter by sector, industry, and price range
    
- **Detailed Stock Pages**: Company profiles enriched with Finnhub API data
    
- **Educational Features**: Portfolio analysis guide and guided tours/onboarding flows
    
- **Advanced User Management**: Balance management and improved profile-related features
    

---

# **2. Environment and System Assumptions**

- **Operating Systems:** Windows, macOS, or Linux
    
- **Language & Runtime:** Java 21
    
- **Framework:** Spring Boot 3.x
    
- **Build Tool:** Maven 3.9+
    
- **Database:**
    
    - `dev` profile → PostgreSQL
        
    - `test` profile → H2 in-memory
        
- **Network:** Internet required for external market-data API (Finnhub)
    
- **Secrets:** API keys and DB credentials passed via environment variables
    
- **Browser:** Any modern browser supports the Thymeleaf UI
    

Assumptions:

- Any external API failure should not prevent the app from functioning.
    
- Users access the system through authenticated sessions (Spring Security).
    

---

# **3. Software Architecture**

The architecture follows a **layered MVC structure**:

- **Web Layer:** Controllers and Thymeleaf pages
    
- **Service Layer:** Business logic (trading, portfolio, watchlist, community, onboarding) and background jobs
    
- **Repository Layer:** Data access through Spring Data JPA
    
- **Domain Layer:** Entities representing the core business objects
    
- **Data Transfer Layer:** Transfer objects between Web and Service layers
    
- **External Service Layer:** Finnhub API for market data and company profiles
    

---

## **3.1 Architecture Diagrams**

> **Figure 1 — High-Level Architecture Overview**  
> This diagram shows the main layers of the system and how they interact. Controllers call services, services talk to repositories, and repositories manage domain entities. Background services and external API calls fit into the same structure.

**Insert diagram here:**  
`![Figure 1 – Architecture Overview](Docs/sprint3/architecture-overview.png)`

---

## **3.2 Architectural Rationale**

The system uses MVC because:

- It matches course material and grading expectations
    
- It keeps views separated from business logic
    
- It allows clean layering and easier testing
    
- It reduces complexity when adding new controllers and pages
    

The architecture avoids unnecessary components and groups related services together to keep the design simple.

---

## **3.3 Sprint 3 Architectural Changes**

Sprint 3 introduced community and educational features while preserving the existing layered MVC structure:

- **Post Entity & CommunityController**: New domain and web components to support social/community content.
    
- **PostService & PostRepository**: Service and persistence layer support for creating, listing, and managing posts.
    
- **TourController and Tour Tracking Logic**: Guides users through the UI with server-rendered steps and state to support onboarding and education.
    
- **DataInitializer**: Seeds demo users, sample posts, and initial stock data for realistic demos and testing.
    
- **Finnhub Integration Enhancements**: Extended usage of the Finnhub API to fetch company profiles and richer stock details, not just quotes.
    
- **Caching & Error Handling Extensions**: Reused and extended existing caching and error-handling strategies for the increased volume of external API calls and community traffic.
    

These changes keep responsibilities aligned with the MVC layers: controllers handle HTTP requests, services encapsulate business and integration logic, and repositories manage persistence. Community and educational features are integrated into the same structure without breaking existing behavior.

---

# **4. System Decomposition**

### **Packages and Their Roles**

- **`web`** — All controllers in the Web Layer, including:
    
    - `AuthController`, `UserController`
        
    - `DashboardController`, `PortfolioController`, `TransactionController`, `StockController`
        
    - `WatchlistController`, `HelpController`, `ResetController`
        
    - **Sprint 3:** `CommunityController` (community hub), `TourController` (guided tours/onboarding)
        
- **`service`** — Business logic, market data, caching, scheduled tasks, and portfolio reset, including:
    
    - `AuthService`, `UserService`
        
    - `PortfolioService`, `TransactionService`, `WatchlistService`
        
    - `MarketDataService`, `FinnhubService`, `CacheService`
        
    - `ScheduledTaskService`, `PortfolioResetService`
        
    - **Sprint 3:** `PostService` (community posts), tour tracking logic (tour-related service if implemented)
        
- **`repo`** — Spring Data JPA repositories for:
    
    - `UserRepository`, `PortfolioRepository`, `TransactionRepository`
        
    - `StockRepository`, `WatchlistRepository`, `MarketDataRepository`
        
    - **Sprint 3:** `PostRepository`
        
- **`domain`** — Entities representing the core business objects:
    
    - `User`, `Portfolio`, `Transaction` (with `BuyTransaction`, `SellTransaction` subclasses), `Stock`, `MarketData`, `Watchlist`
        
    - **Sprint 3:** `Post` (community forum posts)
        
- **`config`** — Security & application configuration:
    
    - Spring Security configuration (authentication, authorization, password encoding)
        
    - Application-level configuration (datasource, API keys, CORS, etc.)
        
    - **Sprint 3:** `DataInitializer` component for data seeding (may live in `config` or a dedicated bootstrap package)
        
- **Supporting Packages:**
    
    - `dto` — Data Transfer Objects between Web and Service layers
        
    - `exception` — Custom exceptions and global exception handlers
        
    - `event` — Domain/application events (if used)
        
    - `util` — Shared utilities (e.g., formatting, validation helpers)
        

---

# **5. CRC Cards**

Below is a **representative subset** of CRC cards for the main classes.

---

### **Class Name:** User

**Parent:** None  
**Subclasses:** None

**Responsibilities:**

- Store user credentials and profile info
    
- Associate with transactions, portfolio, and watchlist
    
- Provide user identity for trading and access control
    

**Collaborators:**

- Portfolio
    
- Watchlist
    
- Transaction
    
- UserService
    
- AuthService
    

---

### **Class Name:** Portfolio

**Parent:** None  
**Subclasses:** None

**Responsibilities:**

- Track user holdings and quantities
    
- Compute average cost, total value, and profit/loss
    
- Provide summary for dashboard and analysis pages
    

**Collaborators:**

- PortfolioService
    
- Stock
    
- Transaction
    
- PortfolioRepository
    

---

### **Class Name:** Transaction

**Parent:** None (abstract)  
**Subclasses:** `BuyTransaction`, `SellTransaction`

**Responsibilities:**

- Represent a stock trade (symbol, quantity, price, timestamp)
    
- Allow services to record buy/sell operations
    
- Support transaction history views and reconciliation
    

**Collaborators:**

- TransactionService
    
- Stock
    
- User
    
- TransactionRepository
    
- PortfolioService
    

---

### **Class Name:** MarketData

**Parent:** None  
**Subclasses:** None

**Responsibilities:**

- Store latest price and metadata for a symbol
    
- Provide locally cached values for quotes and related data
    
- Allow UI to remain responsive when the external API is unavailable
    

**Collaborators:**

- MarketDataService
    
- FinnhubService
    
- MarketDataRepository
    

---

### **Class Name:** Watchlist

**Parent:** None  
**Subclasses:** None

**Responsibilities:**

- Track symbols/stocks the user follows
    
- Provide the list used in watchlist views and dashboard
    
- Persist per-user watchlist entries
    

**Collaborators:**

- WatchlistService
    
- User
    
- WatchlistRepository
    

---

### **Class Name:** Post

**Parent:** None  
**Subclasses:** None

**Responsibilities:**

- Store community forum posts with content, timestamps, and metadata
    
- Associate each post with the User who authored it
    
- Support retrieval for community feed and post detail views
    

**Collaborators:**

- User
    
- PostService
    
- CommunityController
    
- PostRepository
    

---

### **Class Name:** DataInitializer

**Parent:** None  
**Subclasses:** None

**Responsibilities:**

- Seed the database at startup with initial stock data
    
- Create demo users with initial balances and sample portfolios
    
- Insert sample community posts and trading discussions for demos
    

**Collaborators:**

- UserRepository
    
- StockRepository
    
- PostRepository
    
- (Optionally) PortfolioRepository, TransactionRepository
    

---

# **6. Components & Responsibilities**

---

## **6.1 User & Auth Components**

> **Figure 2 — User and Auth Components**  
> Shows how `AuthController` and `UserController` interact with `AuthService`, `UserService`, and `UserRepository`. It also captures integration with Spring Security for login, registration, and session handling.

`![Figure 2 – User & Auth Components](Docs/sprint3/user-auth-components.png)`

---

## **6.2 Trading & Portfolio Components**

> **Figure 3 — Trading and Portfolio Components**  
> Shows `DashboardController`, `PortfolioController`, `StockController`, `TransactionController`, and `WatchlistController` alongside the services and repositories they use. It highlights how trading actions update the portfolio and transaction history, and how stocks and watchlists are rendered.

`![Figure 3 – Trading & Portfolio](Docs/sprint3/trading-portfolio-components.png)`

---

## **6.3 Background Services & External API**

> **Figure 4 — Background Services and API**  
> Shows `ScheduledTaskService`, `PortfolioResetService`, `MarketDataService`, `FinnhubService`, and `CacheService`, plus the Finnhub external API. It illustrates how market data is refreshed, cached, and used, and how portfolio reset routines operate.

`![Figure 4 – Background Services & API](Docs/sprint3/background-services-api.png)`

---

## **6.4 Community & Educational Components**

> **Figure 5 — Community and Educational Features**  
> Shows `CommunityController`, `PostService`, `PostRepository`, `Post`, `TourController`, tour-related services, and `DataInitializer`, and how they integrate with `User`, `AuthService`, and the existing Finnhub-backed market data services.

`![Figure 5 – Community Features](Docs/sprint3/community-components.png)`

This diagram emphasizes:

- Request flow for creating and viewing posts
    
- How community features reuse user identity and security
    
- How guided tours/onboarding are implemented within the MVC structure
    
- How data seeding supports demo and educational use cases
    

---

# **7. Key Workflows**

---

### **Login + Registration**

- User submits login or registration form via the web UI.
    
- `AuthController` receives the request and performs validation.
    
- `AuthController` delegates to `AuthService` and `UserService` for authentication or user creation.
    
- `UserService` persists new users via `UserRepository` (passwords encoded).
    
- On success, Spring Security establishes an authenticated session; user is redirected to the dashboard.
    

---

### **Trading Workflow**

- User submits a buy or sell order from the dashboard or stock page.
    
- Request hits `TransactionController`.
    
- `TransactionController` delegates to `TransactionService` to validate:
    
    - Sufficient balance for buys
        
    - Sufficient holdings for sells
        
- On success, `TransactionService` creates a `Transaction` instance and saves it via `TransactionRepository`.
    
- `PortfolioService` updates the user’s `Portfolio` holdings and aggregates via `PortfolioRepository`.
    
- Updated portfolio and transaction history are displayed on the dashboard/portfolio views.
    

---

### **Market Data Retrieval**

- User opens a page that displays stock prices (dashboard, stock list, watchlist, or detail page).
    
- `StockController` calls `MarketDataService` to obtain quote information.
    
- `MarketDataService` decides whether to use cached `MarketData` or fetch fresh data:
    
    - For fresh data, it calls `FinnhubService` which uses the external Finnhub API.
        
- `MarketDataService` updates `MarketDataRepository` and cache with new results.
    
- Controller renders the view with up-to-date prices and metadata.
    

---

### **Portfolio Reset**

- User triggers a portfolio reset from the UI.
    
- Request is routed to `ResetController`.
    
- `ResetController` calls `PortfolioResetService`.
    
- `PortfolioResetService` collaborates with:
    
    - `PortfolioService` to clear or reinitialize holdings
        
    - `TransactionService` to remove or archive transaction history
        
    - `UserService` if user-level fields (e.g., balance) must be reset
        
- Once complete, controller redirects user to a fresh dashboard/portfolio view.
    

---

### **Community Post Creation**

- User navigates to the Community Hub page.
    
- User fills out and submits a “new post” form.
    
- `CommunityController` receives the POST request, checks authentication, and maps the form data to a `Post` DTO/entity.
    
- `CommunityController` delegates to `PostService` to:
    
    - Validate the post content (non-empty, within length limits).
        
    - Associate the `Post` with the currently authenticated `User`.
        
- `PostService` persists the post via `PostRepository`.
    
- `CommunityController` redirects back to the community feed, which now includes the new post at the top.
    

---

### **Stock Detail Retrieval**

- User clicks a specific stock from the dashboard, watchlist, or stock list.
    
- Request is routed to `StockController` (e.g., `GET /stocks/{symbol}`).
    
- `StockController` calls service-layer logic (e.g., `StockService` / `MarketDataService`) to:
    
    - Load static stock entity data from `StockRepository`.
        
    - Retrieve or refresh detailed company profile from Finnhub via `FinnhubService`.
        
- `MarketDataService` caches this enriched company profile data to reduce repeated API calls.
    
- Controller returns a Thymeleaf view showing:
    
    - Latest quote and key statistics
        
    - Company information (sector, industry, description, etc.)
        
    - Optional educational tips or portfolio context.
        

---

### **Enhanced Stock Filtering**

- User applies filters (sector, industry, price range, etc.) on the stock list page.
    
- Filter parameters are sent as query parameters to `StockController`.
    
- `StockController` passes these parameters to a service (e.g., `StockService` / `MarketDataService`) which:
    
    - Constructs filter criteria and queries stocks via `StockRepository`, or
        
    - Filters an in-memory list based on retrieved data and cached market info.
        
- Filtered results are returned to the controller.
    
- Controller renders a filtered stocks view that shows only the matching securities.
    

---

# **8. Error & Exception Strategy**

- **Invalid user input:**
    
    - Leads to 400-level responses or validation errors shown on forms.
        
    - Validation handled in controllers and DTOs using standard validation annotations and error binding.
        
- **Business rule violations:**
    
    - Thrown from the service layer (e.g., selling more shares than owned, insufficient balance).
        
    - Mapped to 409 (Conflict) or 422 (Unprocessable Entity) with clear error messages for the user.
        
- **External API failure:**
    
    - When Finnhub requests fail or time out, the system falls back to the most recent cached `MarketData`.
        
    - UI shows a “data may be stale” notice so users know the data might not be real-time.
        
- **Database errors:**
    
    - Small, idempotent operations may be retried depending on the failure type.
        
    - User-facing responses use 503 (Service Unavailable) or 409 (Conflict) where appropriate.
        
- **Security:**
    
    - 401 for unauthenticated requests to protected resources.
        
    - 403 for authenticated users attempting to access unauthorized resources (e.g., another user’s portfolio).
        
    - Sensitive operations (trades, posting) require an authenticated session.
        

Global exception handling and logging are used to ensure errors are captured, categorized, and returned as consistent responses.

---

# **9. External Dependencies**

- **Finnhub Stock Market API**
    
    - Provides real-time and delayed quote data.
        
    - **Sprint 3:** Also used to fetch company profiles and richer stock details for stock detail pages.
        
- **Spring Boot / Spring MVC / Spring Security / Spring Data JPA**
    
    - Core application framework, MVC support, security, and data access.
        
- **PostgreSQL**
    
    - Primary relational database in the `dev` profile.
        
- **H2**
    
    - In-memory relational database used for tests.
        
- **Bootstrap + Thymeleaf**
    
    - Server-side rendered UI with Bootstrap-based styling and Thymeleaf templates.
        
- **Data Seeding / Initialization**
    
    - `DataInitializer` component seeds demo users, initial stocks, and sample community posts at application startup to support demos and testing.
