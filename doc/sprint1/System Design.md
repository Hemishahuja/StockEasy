# CRC Cards :

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

