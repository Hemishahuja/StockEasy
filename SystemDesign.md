 StockEasy – System Design Document (Sprint 2–3)

Course: EECS 3311 – Software Design    
Project: StockEasy    
Team: StockEasy    
Version: Sprint 2–3    
Date: 2025-11-17

\---

 Table of Contents

1\. Purpose and Scope    
2\. Overall Architecture (MVC)    
3\. Main Components and Data Flow    
4\. CRC Cards    
   4.1 Controllers    
   4.2 Services    
   4.3 Domain / Entities    
5\. Software Architecture Diagram (description)    
6\. Future Improvements

\---

 1\. Purpose and Scope

This document describes the system design for StockEasy for Sprints 2–3.    
It focuses on the main backend structure (controllers, services, domain classes) and how they work together to support:

\- Viewing stocks and stock details    
\- Portfolio dashboard (holdings / history)    
\- Watchlist page    
\- Market data integration (real/sim)    
\- Basic help / support pages

The goal is to show a clear MVC structure with consistent class names and responsibilities.

\---

 2\. Overall Architecture (MVC)

StockEasy is built as a Spring Boot MVC web application.

\- View (V): Thymeleaf templates under \`src/main/resources/templates\`    
  \- \`dashboard/index.html\`    
  \- \`portfolio/dashboard.html\`, \`portfolio/history.html\`    
  \- \`stocks/list.html\`, \`stocks/detail.html\`    
  \- \`watchlist/index.html\`    
  \- \`help/index.html\`    
  \- \`auth/login.html\`, \`auth/register.html\`

\- Controller (C): Web controllers in \`com.example.stockeasy.web\`    
  \- \`DashboardController\`    
  \- \`PortfolioController\`    
  \- \`StockController\`    
  \- \`WatchlistController\`    
  \- \`HelpController\`    
  \- \`AuthController\`

\- Model (M): Domain, services, and repositories    
  \- Domain/entities in \`com.example.stockeasy.domain\`    
  \- Services in \`com.example.stockeasy.service\`    
  \- Repositories (DB / in-memory) in \`com.example.stockeasy.repo\`

External services (stock price API) are wrapped by \`MarketDataService\`, so the rest of the system does not talk to the API directly.

\---

 3\. Main Components and Data Flow

Typical HTTP request flow:

1\. User clicks a link or button in the browser (e.g., Stocks → Details).  
2\. The request hits a Controller method (e.g., \`StockController.getStockDetail\`).  
3\. The controller calls one or more Service classes (\`StockService\`, \`PortfolioService\`, etc.).  
4\. Services use Repositories to read/write domain objects (\`Stock\`, \`Portfolio\`, \`Trade\`, \`WatchlistItem\`, \`User\`).  
5\. The controller puts the domain objects into the Model.  
6\. A Thymeleaf view renders the final HTML page.

For market data, \`StockController\` → \`MarketDataService\` → external stock API (or simulated data), then back through the same flow.

\---

 4\. CRC Cards

 4.1 Controller CRC Cards

 4.1.1 \`StockController\`

\- Responsibilities  
  \- Handle \`/stocks\` routes (list, filter by sector/industry, details).  
  \- Load active stocks and pass them to \`stocks/list.html\`.  
  \- Load a single \`Stock\` for the details page.  
  \- Expose REST endpoints for latest market data and intraday data.  
\- Collaborators  
  \- \`StockService\`  
  \- \`MarketDataService\`  
  \- \`UserService\`

\---

 4.1.2 \`PortfolioController\`

\- Responsibilities  
  \- Handle \`/portfolio\` routes (dashboard and history).  
  \- Show holdings, total portfolio value, cash/buying power.  
  \- Expose buy/sell endpoints (or redirect to the portfolio tab).  
\- Collaborators  
  \- \`PortfolioService\`  
  \- \`TransactionService\` (or equivalent trade service)  
  \- \`UserService\`  
  \- \`StockService\`

\---

 4.1.3 \`WatchlistController\`

\- Responsibilities  
  \- Handle \`/watchlist\` route.  
  \- Display current user’s watchlist items in a table (symbol, price, notes).  
  \- Add and remove stocks from the watchlist.  
\- Collaborators  
  \- \`WatchlistService\`  
  \- \`UserService\`  
  \- \`StockService\`

\---

 4.1.4 \`DashboardController\`

\- Responsibilities  
  \- Handle \`/\` or \`/dashboard\` route.  
  \- Show a simple overview (e.g., portfolio value, quick links).  
\- Collaborators  
  \- \`PortfolioService\`  
  \- \`MarketDataService\`  
  \- \`UserService\`

\---

 4.1.5 \`HelpController\`

\- Responsibilities  
  \- Handle \`/help\` routes.  
  \- Serve the “Basics” page and simple FAQ / explanations.  
\- Collaborators  
  \- None (only uses templates for now).

\---

 4.1.6 \`AuthController\`

\- Responsibilities  
  \- Handle login and registration pages.  
  \- Delegate actual authentication/registration logic to service layer.  
\- Collaborators  
  \- \`UserService\`  
  \- Spring Security configuration

\---

 4.2 Service CRC Cards

 4.2.1 \`StockService\`

\- Responsibilities  
  \- Provide methods to get all active stocks.  
  \- Find stocks by ID, symbol, sector, or industry.  
  \- Apply filters (above/below price, etc.).  
\- Collaborators  
  \- \`StockRepository\`  
  \- \`MarketDataService\` (when refreshing prices)

\---

 4.2.2 \`PortfolioService\`

\- Responsibilities  
  \- Maintain a user’s holdings and trades.  
  \- Compute portfolio statistics (market value, profit, etc.).  
  \- Provide data for portfolio dashboard and history views.  
\- Collaborators  
  \- \`PortfolioRepository\`  
  \- \`TradeRepository\`  
  \- \`StockService\`  
  \- \`MarketDataService\`

\---

 4.2.3 \`WatchlistService\`

\- Responsibilities  
  \- Add and remove \`WatchlistItem\` entries for a user.  
  \- Fetch the watchlist with latest prices for display.  
  \- Optionally manage alert settings.  
\- Collaborators  
  \- \`WatchlistRepository\`  
  \- \`StockService\`  
  \- \`MarketDataService\`  
  \- \`UserService\`

\---

 4.2.4 \`MarketDataService\`

\- Responsibilities  
  \- Call the external market data API (or simulation mode).  
  \- Cache / refresh latest prices for symbols.  
  \- Provide intraday data for charting.  
\- Collaborators  
  \- External stock price API  
  \- \`StockService\`  
  \- \`PortfolioService\`  
  \- \`WatchlistService\`

\---

 4.2.5 \`UserService\`

\- Responsibilities  
  \- Load users for authentication.  
  \- Register new users.  
  \- Provide user details to other services/controllers.  
\- Collaborators  
  \- \`UserRepository\`  
  \- Spring Security

\---

 4.3 Domain / Entity CRC Cards

 4.3.1 \`User\`

\- Responsibilities  
  \- Represent a registered user (id, username, password, profile info).  
  \- Own a portfolio and a watchlist.  
\- Collaborators  
  \- \`Portfolio\`  
  \- \`WatchlistItem\`  
  \- Spring Security user details

\---

 4.3.2 \`Stock\`

\- Responsibilities  
  \- Represent a single stock (id, symbol, companyName, sector, industry).  
  \- Hold pricing fields (currentPrice, previousClose, priceChangePercent, volume, marketCap).  
\- Collaborators  
  \- \`MarketDataService\`  
  \- \`Portfolio\` / holdings  
  \- \`WatchlistItem\`

\---

 4.3.3 \`Portfolio\`

\- Responsibilities  
  \- Represent a user’s overall investment account.  
  \- Keep a collection of holdings and/or trades.  
  \- Store cash and buying power fields.  
\- Collaborators  
  \- \`User\`  
  \- \`Trade\` / \`Transaction\`  
  \- \`Stock\`  
  \- \`PortfolioService\`

\---

 4.3.4 \`Trade\` (or \`Transaction\`)

\- Responsibilities  
  \- Represent a single buy or sell trade for a stock.  
  \- Track symbol, quantity, price, timestamp, and type (BUY/SELL).  
\- Collaborators  
  \- \`Portfolio\`  
  \- \`Stock\`  
  \- \`PortfolioService\`

\---

 4.3.5 \`WatchlistItem\`

\- Responsibilities  
  \- Represent one row in the watchlist.  
  \- Store symbol, optional last price, and user note.  
\- Collaborators  
  \- \`User\`  
  \- \`Stock\`  
  \- \`WatchlistService\`

\---

 5\. Software Architecture Diagram (description)

In the PDF version, we will include a diagram showing the MVC layers and main components.    
The diagram follows the standard Spring MVC pattern studied in class.

Suggested diagram layout:

\- Top layer – Client  
  \- Box: Browser / StockEasy UI    
    \- Arrows to \`AuthController\`, \`DashboardController\`, \`StockController\`, \`PortfolioController\`, \`WatchlistController\`, \`HelpController\`.

\- Middle layer – Controllers & Services  
  \- Row of controller boxes:  
    \- \`AuthController\`, \`DashboardController\`, \`StockController\`, \`PortfolioController\`, \`WatchlistController\`, \`HelpController\`.  
  \- Below them, row of service boxes:  
    \- \`UserService\`, \`StockService\`, \`PortfolioService\`, \`WatchlistService\`, \`MarketDataService\`.

  \- Arrows:  
    \- Controllers → Services they use (as listed in CRC cards).  
    \- \`StockService\`, \`PortfolioService\`, \`WatchlistService\` → \`MarketDataService\` when they need live prices.

\- Bottom layer – Repositories / Data  
  \- Boxes: \`UserRepository\`, \`StockRepository\`, \`PortfolioRepository\`, \`TradeRepository\`, \`WatchlistRepository\`.  
  \- Box: Database or in-memory storage connected to all repositories.

\- External System  
  \- Box on the right: External Market Data API.  
  \- Arrow from \`MarketDataService\` to the external API box.

The diagram clearly shows:

\- UI → Controllers → Services → Repositories → Data    
\- External API integration through \`MarketDataService\`, not directly from controllers.

This matches the MVC architecture used in the course.

\---

 6\. Future Improvements

\- Move more business rules into the services (e.g., validation, analytics).    
\- Replace in-memory storage with a real database for trades and watchlist items.    
\- Add more endpoints for analytics (profit %, win rate, trade frequency) that connect to the “Simple, Clean Interface” card in Trello.    
\- Use proper DTOs for REST APIs instead of returning entities directly.

\---

