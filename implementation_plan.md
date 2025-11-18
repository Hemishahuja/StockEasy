# Implementation Plan

## Fix Thymeleaf Template JavaScript Expression Issues

The stock page is showing a whitelabel error due to incorrect Thymeleaf expression handling in JavaScript onclick attributes. This plan addresses the immediate template errors while implementing proper AJAX integration with existing backend endpoints and user context extraction.

## Types

No new type definitions required. The implementation will use existing Stock, User, and Watchlist domain models with their current field structures.

## Files

**Existing files to be modified:**
- `src/main/resources/templates/stocks/list.html` - Fix Thymeleaf expressions in onclick attributes and add proper JavaScript implementation
- `src/main/java/com/example/stockeasy/web/WatchlistController.java` - Add REST endpoint for AJAX watchlist operations
- `src/main/java/com/example/stockeasy/web/PortfolioController.java` - Add REST endpoint for AJAX buy operations

**Configuration updates:**
- No configuration file changes required

## Functions

**New functions in list.html:**
- `addToWatchlistAjax(button, stockId)` - AJAX function to add stock to watchlist with proper error handling
- `quickBuyAjax(button, stockId)` - AJAX function to initiate stock purchase with user context
- `extractUserId()` - Helper function to get current user ID from template context
- `handleAjaxResponse(response, successMessage, errorMessage)` - Generic AJAX response handler

**Modified functions:**
- `addToWatchlist()` - Replace with `addToWatchlistAjax()`
- `quickBuy()` - Replace with `quickBuyAjax()`

**Backend REST endpoints to add:**
- `POST /api/watchlist/add` - AJAX endpoint for adding stocks to watchlist
- `POST /api/portfolio/buy` - AJAX endpoint for initiating stock purchases

## Classes

**No new classes required.**

**Modified classes:**
- `WatchlistController` - Add `@PostMapping("/api/add")` method with JSON response
- `PortfolioController` - Add `@PostMapping("/api/buy")` method with JSON response

## Dependencies

No new dependencies required. The implementation uses existing Spring Boot, Thymeleaf, and Bootstrap dependencies already present in the project.

## Testing

**Template validation:**
- Verify Thymeleaf expressions render correctly without whitelabel errors
- Test JavaScript functions with valid and invalid stock IDs
- Confirm user ID extraction from session context

**Integration testing:**
- Test AJAX calls to new REST endpoints
- Verify success/error toast notifications display properly
- Confirm existing backend functionality remains intact

## Implementation Order

1. **Fix immediate Thymeleaf template errors** - Replace problematic onclick expressions with data attributes
2. **Add AJAX JavaScript functions** - Implement proper frontend handlers with error handling
3. **Create REST API endpoints** - Add JSON endpoints for watchlist and buy operations
4. **Integrate user context extraction** - Implement user ID extraction from Thymeleaf context
5. **Test template rendering** - Verify no whitelabel errors and proper JavaScript execution
6. **Test full integration** - Verify AJAX calls work with backend endpoints
