# **StockEasy - System Design Document (Sprint 3)**

**Course:** EECS3311 – SOFTWARE DESIGN  

**Team Name:** StockEasy
  
**Team Members:**
| Name | Role |
|-------|------|
| Arushi | Backend |
| Divy   | Dashboard / UX handoff  |
| Hemish | Scrum Master / Developer  |
| Lama   | Scrum / Project tracking / Backend coordination  |
| Mark   | QA / Project Ops / Documentation |

---

# **Table of Contents**

1. Overview
2. Environment & System Assumptions
3. Software Architecture
    - 3.1 Architecture Diagram
    - 3.2 Architectural Rationale
4. System Decomposition
5. CRC Cards
6. Components & Responsibilities
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
- **Service Layer:** Business logic (trading, portfolio, watchlist, onboarding) and background jobs
- **Repository Layer:** Data access through Spring Data JPA
- **Domain Layer:** Entities representing the core business objects
- **Data Transfer Layer:** Transfer objects between Web and Service layers
- **External Service:** Finnhub API for market data

### **3.1 Architecture Diagrams**

> **Figure 1 — High-Level Architecture Overview**  
> This diagram shows the main layers of the system and how they interact. Controllers call services, services talk to repositories, and repositories manage domain entities. Background services and external API calls fit into the same structure.

**Insert diagram here:**  
`![Figure 1 – Architecture Overview](Docs/sprint3/architecture-overview.png)`

### **3.2 Architectural Rationale**

The system uses MVC because:
- It matches course material and grading expectations
- It keeps views separated from business logic
- It allows clean layering and easier testing
- It reduces complexity when adding new controllers and pages

The architecture avoids unnecessary components and groups related services together to keep the design simple.

---

# **4. System Decomposition**

### **Packages and Their Roles**

- **`web`** — All controllers in the Web Layer
- **`service`** — Business logic, market data, caching, scheduled tasks, portfolio reset
- **`repo`** — JPA repositories for Users, Stocks, Portfolio, Watchlist, MarketData, Transactions
- **`domain`** — Entities representing the core objects
- **`config`** — Security & application configuration
- **Supporting:** `dto`, `exception`, `event`, `util`


---

# **5. CRC Cards**

Below is a **representative subset** of CRC cards for the main classes.  

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
- Compute average cost, total value, and P/L
- Provide summary for dashboard and analysis pages

**Collaborators:**
- PortfolioService
- Stock
- Transaction
- PortfolioRepository

---

### **Class Name:** Transaction

**Parent:** None (abstract)  
**Subclasses:** BuyTransaction, SellTransaction  
**Responsibilities:**
- Represent a stock trade (symbol, quantity, price, timestamp)
- Allow services to record buy/sell operations
- Support history views and reconciliation

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
- Provide local cached values
- Allow UI to remain responsive when API is unavailable

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
- Provide list for watchlist view

**Collaborators:**
- WatchlistService
- User
- WatchlistRepository

---

# **6. Components & Responsibilities**


## **6.1 User & Auth Components**

> **Figure 2 — User and Auth Components**  
> Shows how AuthController and UserController interact with AuthService, UserService, and UserRepository.

`![Figure 2 – User & Auth Components](Docs/sprint3/user-auth-components.png)`

---

## **6.2 Trading & Portfolio Components**

> **Figure 3 — Trading and Portfolio Components**  
> Shows DashboardController, PortfolioController, StockController, TransactionController, and WatchlistController alongside the services and repositories they use.

`![Figure 3 – Trading & Portfolio](Docs/sprint3/trading-portfolio-components.png)`

---

## **6.3 Background Services & External API**

> **Figure 4 — Background Services and API**  
> Shows ScheduledTaskService, PortfolioResetService, MarketDataService, FinnhubService, and CacheService, plus the Finnhub external API.

`![Figure 4 – Background Services & API](Docs/sprint3/background-services-api.png)`

---

# **7. Key Workflows**

### **Login + Registration**
- User submits form
- AuthController → AuthService → UserService → UserRepository
- Password encoded, user saved

### **Trading Workflow**
- Buy/sell request hits TransactionController
- TransactionService validates trade
- PortfolioService updates holdings
- Data stored via TransactionRepository & PortfolioRepository

### **Market Data Retrieval**
- StockController → MarketDataService
- MarketDataService → FinnhubService → External API
- Cached and saved via MarketDataRepository

### **Portfolio Reset**
- ResetController triggers PortfolioResetService
- Collaborates with PortfolioService, TransactionService, UserService
- Resets portfolio to initial state

---

# **8. Error & Exception Strategy**

- **Invalid user input:**
    - 400-level responses or form errors
    - Validation handled in controllers and DTOs
- **Business rule violations:**
    - Thrown from service layer (e.g., selling more shares than owned)
    - Mapped to 409 or 422
- **External API failure:**
    - Fallback: use cached MarketData
    - UI shows a “data may be stale” notice
- **Database errors:**
    - Retry small operations
    - 503 or 409 depending on cause
- **Security:**
    - 401 for unauthenticated, 403 for unauthorized

---

# **9. External Dependencies**

- **Finnhub Stock Market API** (market data)
- **Spring Boot / Spring MVC / Spring Security / Spring Data JPA**
- **PostgreSQL** for dev
- **H2** for tests
- **Bootstrap + Thymeleaf** for UI

