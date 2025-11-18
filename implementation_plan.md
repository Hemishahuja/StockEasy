# Implementation Plan

This implementation adds a reset sandbox button to the portfolio dashboard that resets all current stocks, portfolio holdings, and transaction history to their default state while restoring the user's cash balance to the initial $100,000.

## Overview

Add a reset sandbox functionality that clears all user portfolio data and restores default state for testing and experimentation purposes in the stock trading simulator.

## Types

Add new request/response types for reset operations and update existing domain validation.

- **ResetPortfolioRequest**: Request object for portfolio reset operations
  - `userId` (Long): User identifier
  - `confirmReset` (Boolean): Explicit confirmation flag
  - `timestamp` (LocalDateTime): Request timestamp

- **ResetPortfolioResponse**: Response object for reset operations  
  - `success` (Boolean): Operation success status
  - `message` (String): Success/error message
  - `resetAt` (LocalDateTime): Timestamp of reset operation
  - `initialCashBalance` (BigDecimal): Restored cash balance amount

- **PortfolioResetEvent**: Domain event for audit logging
  - `userId` (Long): User who performed reset
  - `previousHoldings` (List<StockHolding>): Snapshot of holdings before reset
  - `timestamp` (LocalDateTime): When reset occurred

## Files

Create new service and controller methods, add UI components, and update configuration.

**New files to be created:**
- `src/main/java/com/example/stockeasy/service/PortfolioResetService.java` - Core reset business logic
- `src/main/java/com/example/stockeasy/web/ResetController.java` - REST API endpoints for reset operations
- `src/main/java/com/example/stockeasy/event/PortfolioResetEvent.java` - Audit event for reset operations
- `src/main/java/com/example/stockeasy/dto/ResetPortfolioRequest.java` - Request DTO
- `src/main/java/com/example/stockeasy/dto/ResetPortfolioResponse.java` - Response DTO
- `src/main/java/com/example/stockeasy/dto/StockHolding.java` - DTO for audit snapshot

**Existing files to be modified:**
- `src/main/resources/templates/portfolio/dashboard.html` - Add reset button and confirmation modal
- `src/main/java/com/example/stockeasy/service/PortfolioService.java` - Add bulk delete methods
- `src/main/java/com/example/stockeasy/service/TransactionService.java` - Add transaction history cleanup
- `src/main/java/com/example/stockeasy/service/UserService.java` - Add cash balance reset method
- `src/main/java/com/example/stockeasy/repo/PortfolioRepository.java` - Add bulk delete operations
- `src/main/java/com/example/stockeasy/repo/TransactionRepository.java` - Add user transaction deletion

**Configuration updates:**
- Add security configuration for reset endpoints
- Update application properties if needed for audit logging

## Functions

Add new functions for reset operations and modify existing ones for bulk operations.

**New functions:**
- `PortfolioResetService.resetUserPortfolio(userId: Long, request: ResetPortfolioRequest): ResetPortfolioResponse`
  - Validates reset request and performs complete portfolio reset
  - Creates audit trail and handles transaction rollback on failure
- `PortfolioResetService.createPortfolioSnapshot(userId: Long): List<StockHolding>`
  - Captures current portfolio state for audit purposes
- `PortfolioResetService.validateResetEligibility(userId: Long): Boolean`
  - Checks if user is eligible for reset (cooldown periods, etc.)
- `ResetController.resetPortfolio(request: ResetPortfolioRequest): ResponseEntity<ResetPortfolioResponse>`
  - REST endpoint for portfolio reset with proper error handling

**Modified functions:**
- `PortfolioService.deleteUserPortfolio(userId: Long): void`
  - Add bulk deletion method for all user portfolio entries
- `TransactionService.clearUserTransactions(userId: Long): void`
  - Add method to clear all user transaction history
- `UserService.resetUserCashBalance(userId: Long): BigDecimal`
  - Reset user cash to initial $100,000 and return new balance

**Removed functions:**
- No functions will be removed, only additions and modifications

## Classes

Add new service classes and extend existing ones for reset functionality.

**New classes:**
- `PortfolioResetService` - Handles complete portfolio reset workflow
  - Dependencies: PortfolioService, TransactionService, UserService, PortfolioRepository, TransactionRepository
  - Key methods: resetUserPortfolio, createPortfolioSnapshot, validateResetEligibility
  - Inherits from no base class, uses standard Spring @Service annotation

- `ResetController` - REST API controller for reset operations
  - Dependencies: PortfolioResetService
  - Key methods: resetPortfolio endpoint with CSRF protection
  - Extends standard Spring @Controller with @ResponseBody methods

- `PortfolioResetEvent` - Domain event for audit logging
  - Properties: userId, previousHoldings, timestamp, ipAddress
  - Implements Serializable for event persistence

**Modified classes:**
- `PortfolioService` - Add bulk deletion capabilities
  - Add deleteUserPortfolio method with cascade deletion logic
  - Add validation for concurrent reset operations

- `TransactionService` - Add transaction history cleanup
  - Add clearUserTransactions method with proper transaction boundaries
  - Update existing methods to handle empty portfolio states

**Removed classes:**
- No classes will be removed

## Dependencies

Add minimal dependencies for enhanced functionality and security.

**New dependencies:**
- Spring Security for CSRF protection on reset endpoints
- Jackson for JSON serialization of audit events
- SLF4J for structured logging of reset operations

**Version changes:**
- No version changes required for existing dependencies

**Integration requirements:**
- Ensure proper transaction management for atomic reset operations
- Add audit logging integration if audit system exists
- CSRF token validation for reset button form submission

## Testing

Implement comprehensive testing for reset functionality with proper test isolation.

**Test file requirements:**
- `PortfolioResetServiceTest.java` - Unit tests for reset service logic
- `ResetControllerTest.java` - Integration tests for REST endpoints
- `PortfolioResetIntegrationTest.java` - End-to-end workflow testing

**Existing test modifications:**
- Update `PortfolioControllerTest.java` to include reset button testing
- Modify `PortfolioServiceTest.java` to test bulk deletion methods
- Add test data setup for reset scenario testing

**Validation strategies:**
- Test rollback behavior on partial failures
- Verify audit trail creation and persistence
- Validate security constraints and CSRF protection
- Test concurrent reset attempt handling
- Verify portfolio state restoration to defaults

## Implementation Order

Implement in logical sequence to ensure dependencies are available and testing is possible.

1. Create DTO classes (ResetPortfolioRequest, ResetPortfolioResponse, StockHolding)
2. Add audit event class (PortfolioResetEvent)
3. Extend repository interfaces with bulk deletion methods
4. Implement core reset service (PortfolioResetService)
5. Add REST controller for reset operations (ResetController)
6. Update existing service classes with bulk operations
7. Modify portfolio dashboard template to include reset button
8. Add JavaScript functionality for confirmation modal
9. Implement comprehensive test suite
10. Update security configuration for new endpoints
11. Add audit logging integration
12. Perform end-to-end testing and validation
