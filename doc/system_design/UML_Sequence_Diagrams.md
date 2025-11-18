# StockEasy System - UML Sequence Diagrams

This document provides UML sequence diagrams for key workflows in the StockEasy stock trading simulator system.

## System Overview

The sequence diagrams illustrate the flow of interactions between system components for critical business processes including user authentication, stock trading, portfolio management, and the new portfolio reset functionality.

## 1. User Registration Sequence

```mermaid
sequenceDiagram
    participant User as "User"
    participant AuthController as "AuthController"
    participant AuthService as "AuthService"
    participant UserService as "UserService"
    participant UserRepository as "UserRepository"
    participant PasswordEncoder as "PasswordEncoder"

    User->>AuthController: Submit registration form
    AuthController->>AuthService: validateRegistrationData()
    AuthService->>AuthService: validateEmailFormat()
    AuthService->>AuthService: validatePasswordStrength()
    
    alt Valid data
        AuthController->>UserService: registerUser(username, email, password, firstName, lastName)
        UserService->>UserRepository: existsByUsername(username)
        UserService->>UserRepository: existsByEmail(email)
        
        alt User doesn't exist
            UserService->>PasswordEncoder: encode(password)
            UserService->>UserService: createNewUser()
            UserService->>UserRepository: save(user)
            UserService->>UserService: sendWelcomeEmail()
            UserService-->>AuthController: return user
            AuthController-->>User: Registration successful
        else User already exists
            UserService-->>AuthController: throw UserAlreadyExistsException
            AuthController-->>User: Error: Username/email already taken
        end
    else Invalid data
        AuthController-->>User: Error: Invalid input data
    end
```

## 2. User Login Sequence

```mermaid
sequenceDiagram
    participant User as "User"
    participant AuthController as "AuthController"
    participant AuthService as "AuthService"
    participant UserService as "UserService"
    participant UserRepository as "UserRepository"
    participant SecurityContext as "SecurityContext"

    User->>AuthController: Submit login credentials
    AuthController->>AuthService: authenticateUser(username, password)
    AuthService->>UserRepository: findByUsername(username)
    
    alt User found
        AuthService->>AuthService: passwordEncoder.matches(password, user.hashedPassword)
        
        alt Password valid
            AuthService->>UserService: updateLastLogin(user)
            AuthService->>SecurityContext: setAuthentication(user)
            AuthService->>AuthService: generateJWTToken()
            AuthService-->>AuthController: return authentication result
            AuthController-->>User: Login successful, redirect to dashboard
        else Password invalid
            AuthService->>UserService: incrementFailedLoginAttempts(user)
            AuthService-->>AuthController: throw AuthenticationException
            AuthController-->>User: Error: Invalid credentials
        end
    else User not found
        AuthController-->>User: Error: Invalid credentials
    end
```

## 3. Buy Stock Transaction Sequence

```mermaid
sequenceDiagram
    participant User as "User"
    participant PortfolioController as "PortfolioController"
    participant PortfolioService as "PortfolioService"
    participant TransactionService as "TransactionService"
    participant StockService as "StockService"
    participant UserService as "UserService"
    participant PortfolioRepository as "PortfolioRepository"
    participant TransactionRepository as "TransactionRepository"
    participant UserRepository as "UserRepository"

    User->>PortfolioController: Request buy stock (symbol, quantity)
    PortfolioController->>StockService: getStockBySymbol(symbol)
    StockService-->>PortfolioController: return stock
    
    PortfolioController->>TransactionService: buyStock(userId, stockId, quantity)
    TransactionService->>UserService: getUserById(userId)
    TransactionService->>StockService: getStockById(stockId)
    
    TransactionService->>TransactionService: validateBuyTransaction(user, stock, quantity)
    
    alt Validation successful
        TransactionService->>UserService: withdrawCash(totalCost)
        
        alt Sufficient funds
            TransactionService->>PortfolioService: addToPortfolio(userId, stockId, quantity)
            PortfolioService->>PortfolioRepository: findByUserIdAndStockId(userId, stockId)
            
            alt Existing holding
                PortfolioService->>PortfolioService: updateExistingPortfolio()
                PortfolioService->>PortfolioRepository: save(portfolio)
            else New holding
                PortfolioService->>PortfolioService: createNewPortfolio()
                PortfolioService->>PortfolioRepository: save(portfolio)
            end
            
            TransactionService->>TransactionRepository: save(buyTransaction)
            TransactionService->>UserRepository: save(user)
            TransactionService-->>PortfolioController: return transaction
            PortfolioController-->>User: Buy successful notification
        else Insufficient funds
            TransactionService-->>PortfolioController: throw InsufficientFundsException
            PortfolioController-->>User: Error: Insufficient funds
        end
    else Validation failed
        TransactionService-->>PortfolioController: throw ValidationException
        PortfolioController-->>User: Error: Validation failed
    end
```

## 4. Sell Stock Transaction Sequence

```mermaid
sequenceDiagram
    participant User as "User"
    participant PortfolioController as "PortfolioController"
    participant PortfolioService as "PortfolioService"
    participant TransactionService as "TransactionService"
    participant StockService as "StockService"
    participant UserService as "UserService"
    participant PortfolioRepository as "PortfolioRepository"
    participant TransactionRepository as "TransactionRepository"

    User->>PortfolioController: Request sell stock (symbol, quantity)
    PortfolioController->>StockService: getStockBySymbol(symbol)
    StockService-->>PortfolioController: return stock
    
    PortfolioController->>TransactionService: sellStock(userId, stockId, quantity)
    TransactionService->>StockService: getStockById(stockId)
    TransactionService->>PortfolioRepository: findByUserIdAndStockId(userId, stockId)
    
    alt Portfolio exists
        TransactionService->>TransactionService: validateSellTransaction(user, portfolio, quantity)
        
        alt Validation successful (sufficient shares)
            TransactionService->>UserService: depositCash(proceeds)
            
            TransactionService->>PortfolioRepository: save(sellTransaction)
            
            alt Sell all shares
                TransactionService->>PortfolioRepository: delete(portfolio)
            else Sell partial shares
                TransactionService->>PortfolioService: updatePortfolioQuantity()
                TransactionService->>PortfolioRepository: save(portfolio)
            end
            
            TransactionService-->>PortfolioController: return transaction
            PortfolioController-->>User: Sell successful notification
        else Insufficient shares
            TransactionService-->>PortfolioController: throw InsufficientSharesException
            PortfolioController-->>User: Error: Insufficient shares
        end
    else Portfolio not found
        TransactionService-->>PortfolioController: throw ResourceNotFoundException
        PortfolioController-->>User: Error: Portfolio not found
    end
```

## 5. Portfolio Reset Sequence (New Feature)

```mermaid
sequenceDiagram
    participant User as "User"
    participant ResetController as "ResetController"
    participant PortfolioResetService as "PortfolioResetService"
    participant PortfolioService as "PortfolioService"
    participant TransactionService as "TransactionService"
    participant UserService as "UserService"
    participant PortfolioRepository as "PortfolioRepository"
    participant TransactionRepository as "TransactionRepository"
    participant UserRepository as "UserRepository"

    User->>ResetController: Click reset button, confirm action
    ResetController->>ResetController: validateCsrfToken()
    ResetController->>PortfolioResetService: resetUserPortfolio(userId, request)
    
    PortfolioResetService->>UserRepository: findById(userId)
    
    alt User exists
        PortfolioResetService->>PortfolioResetService: validateResetEligibility(userId)
        
        alt User eligible for reset
            PortfolioResetService->>PortfolioService: createPortfolioSnapshot(userId)
            PortfolioService->>PortfolioRepository: findUserPortfolioWithStockInfo(userId)
            PortfolioRepository-->>PortfolioService: return portfolio list
            PortfolioService-->>PortfolioResetService: return StockHolding list
            
            PortfolioResetService->>PortfolioResetService: performPortfolioReset(userId)
            
            Note over PortfolioResetService: Transaction boundary begins
            
            PortfolioResetService->>TransactionService: clearUserTransactions(userId)
            TransactionService->>TransactionRepository: deleteByUserId(userId)
            TransactionRepository-->>TransactionService: return
            
            PortfolioResetService->>PortfolioService: deleteUserPortfolio(userId)
            PortfolioService->>PortfolioRepository: deleteByUserId(userId)
            PortfolioRepository-->>PortfolioService: return
            
            PortfolioResetService->>UserService: resetUserCashBalance(userId)
            UserService->>UserRepository: findById(userId)
            UserService->>UserService: setCashBalance(100000.00)
            UserService->>UserRepository: save(user)
            UserRepository-->>UserService: return
            
            Note over PortfolioResetService: Transaction boundary ends
            
            PortfolioResetService->>PortfolioResetService: createPortfolioResetEvent()
            PortfolioResetService->>PortfolioResetService: logAuditEvent()
            
            PortfolioResetService-->>ResetController: return ResetPortfolioResponse
            ResetController-->>User: Success notification, refresh page
        else User not eligible
            PortfolioResetService-->>ResetController: throw ResetNotAllowedException
            ResetController-->>User: Error: Reset not allowed at this time
        end
    else User not found
        PortfolioResetService-->>ResetController: throw ResourceNotFoundException
        ResetController-->>User: Error: User not found
    end
```

## 6. Portfolio Dashboard Load Sequence

```mermaid
sequenceDiagram
    participant User as "User"
    participant PortfolioController as "PortfolioController"
    participant PortfolioService as "PortfolioService"
    participant TransactionService as "TransactionService"
    participant StockService as "StockService"
    participant UserService as "UserService"
    participant PortfolioRepository as "PortfolioRepository"
    participant TransactionRepository as "TransactionRepository"

    User->>PortfolioController: Navigate to portfolio dashboard
    PortfolioController->>UserService: getCurrentUser()
    
    par Load portfolio data
        PortfolioController->>PortfolioService: getUserPortfolio(userId)
        PortfolioService->>PortfolioRepository: findUserPortfolioWithStockInfo(userId)
        PortfolioRepository-->>PortfolioService: return portfolio list
        PortfolioService->>PortfolioService: updateCurrentValue() for each portfolio
        PortfolioService->>PortfolioRepository: save() each updated portfolio
        PortfolioService-->>PortfolioController: return portfolio items
    and Load portfolio value
        PortfolioController->>PortfolioService: calculatePortfolioValue(userId)
        PortfolioService->>PortfolioRepository: calculatePortfolioValue(userId)
        PortfolioRepository-->>PortfolioService: return total value
        PortfolioService-->>PortfolioController: return portfolio value
    and Load available stocks
        PortfolioController->>StockService: getActiveStocks()
        StockService->>StockRepository: findByActiveTrue()
        StockRepository-->>StockService: return active stocks
        StockService-->>PortfolioController: return stock list
    and Load recent transactions
        PortfolioController->>TransactionService: getUserTransactionHistory(userId)
        TransactionService->>TransactionRepository: findUserTransactionHistory(userId)
        TransactionRepository-->>TransactionService: return transaction list
        TransactionService-->>PortfolioController: return transaction history
    and Load user profile
        PortfolioController->>UserService: getUserProfile(userId)
        UserService->>UserRepository: findByIdWithRelationships(userId)
        UserRepository-->>UserService: return user with data
        UserService-->>PortfolioController: return user profile
    
    PortfolioController->>PortfolioController: aggregateAllData()
    PortfolioController-->>User: Display portfolio dashboard with all data
```

## 7. Watchlist Management Sequence

```mermaid
sequenceDiagram
    participant User as "User"
    participant WatchlistController as "WatchlistController"
    participant WatchlistService as "WatchlistService"
    participant StockService as "StockService"
    participant WatchlistRepository as "WatchlistRepository"
    participant StockRepository as "StockRepository"

    User->>WatchlistController: Add stock to watchlist
    WatchlistController->>StockService: getStockBySymbol(symbol)
    StockService->>StockRepository: findBySymbol(symbol)
    StockRepository-->>StockService: return stock
    StockService-->>WatchlistController: return stock
    
    WatchlistController->>WatchlistService: addToWatchlist(userId, stockId)
    WatchlistService->>WatchlistRepository: existsByUserAndStock(userId, stockId)
    
    alt Not already in watchlist
        WatchlistService->>WatchlistService: createNewWatchlistEntry()
        WatchlistService->>WatchlistRepository: save(watchlist)
        WatchlistService-->>WatchlistController: return watchlist
        WatchlistController-->>User: Success: Stock added to watchlist
    else Already in watchlist
        WatchlistService-->>WatchlistController: throw AlreadyInWatchlistException
        WatchlistController-->>User: Error: Stock already in watchlist
    end
```

## 8. Market Data Update Sequence

```mermaid
sequenceDiagram
    participant ScheduledTask as "ScheduledTaskService"
    participant MarketDataService as "MarketDataService"
    participant AlphaVantageService as "AlphaVantageService"
    participant StockService as "StockService"
    participant StockRepository as "StockRepository"
    participant PortfolioRepository as "PortfolioRepository"

    ScheduledTask->>MarketDataService: updateStockPricesFromAPI()
    
    loop For each active stock
        MarketDataService->>AlphaVantageService: fetchStockData(symbol)
        AlphaVantageService->>AlphaVantageService: callExternalAPI(symbol)
        
        alt API call successful
            AlphaVantageService-->>MarketDataService: return MarketData
            MarketDataService->>MarketDataService: processMarketData(data)
            MarketDataService->>StockService: updateStockPrice(symbol, newPrice)
            StockService->>StockRepository: findBySymbol(symbol)
            StockService->>StockService: updateStockPrice(stock, newPrice)
            StockService->>StockRepository: save(stock)
            
            Note over StockService: Update dependent portfolio values
            StockService->>PortfolioRepository: findByStockId(stockId)
            PortfolioRepository-->>StockService: return portfolio list
            
            loop For each affected portfolio
                StockService->>StockService: updateCurrentValue()
                StockService->>PortfolioRepository: save(portfolio)
            end
            
            StockService-->>MarketDataService: return updated stock
        else API call failed
            AlphaVantageService-->>MarketDataService: throw AlphaVantageApiException
            MarketDataService->>MarketDataService: logErrorAndContinue()
        end
    end
    
    MarketDataService-->>ScheduledTask: Price update completed
```

## Key Sequence Flow Patterns

### 1. Request Validation Pattern
All user requests follow this pattern:
1. **Input Validation**: Controllers validate input format
2. **Business Validation**: Services validate business rules
3. **Authorization Check**: Security context validation
4. **Data Access**: Repository operations within transactions
5. **Response Formatting**: Controllers format responses

### 2. Transaction Management Pattern
Critical operations use this pattern:
1. **Transaction Begin**: @Transactional annotation
2. **Multiple Operations**: Portfolio, Transaction, User updates
3. **Error Handling**: Rollback on exceptions
4. **Audit Logging**: Post-commit audit events

### 3. Error Handling Pattern
All sequences include comprehensive error handling:
- **Validation Errors**: Input format and business rule violations
- **Business Errors**: Insufficient funds, shares, permissions
- **System Errors**: Database failures, API timeouts
- **User Feedback**: Appropriate error messages and recovery options

### 4. Security Pattern
Security is enforced throughout:
- **Authentication**: User identity verification
- **Authorization**: Permission checks for operations
- **CSRF Protection**: Token validation for state-changing operations
- **Audit Logging**: Security event tracking

These sequence diagrams provide a comprehensive view of the system's dynamic behavior, showing how components interact to deliver the StockEasy functionality.
