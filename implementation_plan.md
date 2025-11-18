# Implementation Plan

[Overview]

The goal is to combine the stocks listing page and market overview page into a single unified page that displays both stock search/filtering functionality and live market data with refresh capabilities.

The stocks page currently provides a static list of stocks with search and filters, while the dashboard has a market overview section with real-time price updates via API. By merging these features, users will have a comprehensive stock market page that combines browsing and real-time monitoring without needing separate navigation.

[Types]

No new type system changes are required since all existing domain models (Stock, MarketData) are already suitable for the combined functionality.

[Files]

Files to be modified include the stocks list template to add market overview section and live price update script, and the dashboard controller to remove or redirect the market endpoint.

- Modify `src/main/resources/templates/stocks/list.html` to add market overview header section above the stock list, including refresh buttons and live price display
- Copy market update JavaScript from `src/main/resources/templates/dashboard/index.html` and adapt for stocks page context
- Modify `src/main/java/com/example/stockeasy/web/DashboardController.java` to change or remove the /market endpoint since market functionality will be in stocks page
- Update navigation in templates if necessary to reflect combined page

[Functions]

No new functions needed; existing controller methods and service calls will be reused.

- Use existing StockController.getAllStocks() for the stock list
- Use existing StockService.getActiveStocks() for market data cards
- Reuse API endpoints in StockController for live market data refresh

[Classes]

No new classes are needed; existing controller and service classes provide all required functionality.

- StockController: Already has API endpoints for market data refresh
- MarketDataService: Provides live price fetching and caching
- StockService: Supplies stock listings

[Dependencies]

No new dependencies are required as the existing Spring Boot stack with Thymeleaf templates supports all needed features.

[Testing]

Testing will focus on validating the combined page loads correctly, live price updates work, and search/filter functionality remains intact.

- Manual testing of page load and stock listing display
- Verify API endpoints for price refresh return correct data
- Test search and filter functionality after combining features

[Implementation Order]

1. Add market overview section to stocks/list.html above the existing stock list
2. Copy and adapt the live price update JavaScript from dashboard/index.html
3. Test the page loads and basic functionality works
4. Modify DashboardController /market endpoint to redirect to /stocks or remove it
5. Update any navigation links that pointed to /market
6. Test live price refresh functionality integrates properly with stock list
