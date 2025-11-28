package com.example.stockeasy.web;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.stockeasy.domain.MarketData;
import com.example.stockeasy.domain.Stock;
import com.example.stockeasy.domain.User;
import com.example.stockeasy.service.MarketDataService;
import com.example.stockeasy.service.StockService;
import com.example.stockeasy.service.UserService;

/**
 * StockController for stock management.
 * Handles stock search, display, and operations.
 */
@Controller
@RequestMapping("/stocks")
public class StockController {

    @Autowired
    private StockService stockService;

    @Autowired
    private MarketDataService marketDataService;

    @Autowired
    private UserService userService;

    /**
     * Display all active stocks
     */
    /**
     * Display all active stocks with optional filters
     */
    @GetMapping
    public String getAllStocks(@RequestParam(required = false) String sector,
            @RequestParam(required = false) String priceRange,
            Model model) {
        List<Stock> stocks = stockService.getActiveStocks();

        // Apply Sector Filter
        if (sector != null && !sector.isEmpty() && !sector.equals("all")) {
            stocks = stocks.stream()
                    .filter(s -> s.getSector() != null && s.getSector().equalsIgnoreCase(sector))
                    .toList();
        }

        // Apply Price Range Filter
        if (priceRange != null && !priceRange.isEmpty()) {
            stocks = stocks.stream()
                    .filter(s -> {
                        BigDecimal price = s.getCurrentPrice();
                        if (price == null)
                            return false;

                        switch (priceRange) {
                            case "0-50":
                                return price.compareTo(BigDecimal.valueOf(50)) < 0;
                            case "50-100":
                                return price.compareTo(BigDecimal.valueOf(50)) >= 0
                                        && price.compareTo(BigDecimal.valueOf(100)) <= 0;
                            case "100-200":
                                return price.compareTo(BigDecimal.valueOf(100)) > 0
                                        && price.compareTo(BigDecimal.valueOf(200)) <= 0;
                            case "200+":
                                return price.compareTo(BigDecimal.valueOf(200)) > 0;
                            case "gainers":
                                return s.isPriceUp();
                            case "losers":
                                return s.isPriceDown();
                            default:
                                return true;
                        }
                    })
                    .toList();
        }

        model.addAttribute("stocks", stocks);
        model.addAttribute("selectedSector", sector);
        model.addAttribute("selectedPriceRange", priceRange);

        // Add user information for JavaScript to extract user ID
        try {
            // Get current authenticated user
            org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                User user = (User) userService.loadUserByUsername(username);
                model.addAttribute("user", user);
            }
        } catch (Exception e) {
            // User is not authenticated or error occurred
            model.addAttribute("user", null);
        }

        return "stocks/list";
    }

    /**
     * Search stocks by symbol
     */
    @GetMapping("/search")
    public String searchStocks(@RequestParam String symbol, Model model) {
        Stock stock = stockService.getStockBySymbol(symbol.toUpperCase());
        model.addAttribute("stock", stock);
        return "stocks/detail";
    }

    /**
     * Display stock details
     */
    @GetMapping("/{stockId}")
    public String getStockDetail(@PathVariable Long stockId, Model model) {
        try {
            // Get all active stocks and find the one with matching ID
            List<Stock> allStocks = stockService.getActiveStocks();
            Stock stock = allStocks.stream()
                    .filter(s -> s.getId().equals(stockId))
                    .findFirst()
                    .orElse(null);

            if (stock == null) {
                model.addAttribute("error", "Stock not found");
                return "redirect:/stocks";
            }

            model.addAttribute("stock", stock);
            return "stocks/detail";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading stock details: " + e.getMessage());
            return "redirect:/stocks";
        }
    }

    /**
     * Display stocks by sector
     */
    @GetMapping("/sector/{sector}")
    public String getStocksBySector(@PathVariable String sector, Model model) {
        List<Stock> stocks = stockService.getStocksBySector(sector);
        model.addAttribute("stocks", stocks);
        model.addAttribute("sector", sector);
        return "stocks/list";
    }

    /**
     * Display stocks by industry
     */
    @GetMapping("/industry/{industry}")
    public String getStocksByIndustry(@PathVariable String industry, Model model) {
        List<Stock> stocks = stockService.getStocksByIndustry(industry);
        model.addAttribute("stocks", stocks);
        model.addAttribute("industry", industry);
        return "stocks/list";
    }

    /**
     * Display stocks above a certain price
     */
    @GetMapping("/above/{price}")
    public String getStocksAbovePrice(@PathVariable BigDecimal price, Model model) {
        List<Stock> stocks = stockService.findStocksAbovePrice(price);
        model.addAttribute("stocks", stocks);
        model.addAttribute("price", price);
        return "stocks/list";
    }

    /**
     * Display stocks below a certain price
     */
    @GetMapping("/below/{price}")
    public String getStocksBelowPrice(@PathVariable BigDecimal price, Model model) {
        List<Stock> stocks = stockService.findStocksBelowPrice(price);
        model.addAttribute("stocks", stocks);
        model.addAttribute("price", price);
        return "stocks/list";
    }

    /**
     * REST API endpoint to refresh market data for a specific symbol.
     * Returns JSON response with refresh status.
     */
    @PostMapping("/api/refresh/{symbol}")
    @ResponseBody
    public ResponseEntity<String> refreshMarketData(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "5min") String interval) {

        try {
            List<MarketData> marketDataList = marketDataService.refreshMarketDataSync(symbol.toUpperCase(), interval);
            String message = String.format("Successfully refreshed %d data points for symbol %s",
                    marketDataList.size(), symbol.toUpperCase());
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to refresh data for symbol %s: %s",
                    symbol.toUpperCase(), e.getMessage());
            return ResponseEntity.badRequest().body(errorMessage);
        }
    }

    /**
     * REST API endpoint to get latest market data for a symbol.
     * Returns the most recent market data point.
     */
    @GetMapping("/api/latest/{symbol}")
    @ResponseBody
    public ResponseEntity<MarketData> getLatestMarketData(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "5min") String interval) {

        try {
            MarketData marketData = marketDataService.getLatestMarketDataForSymbolSync(symbol.toUpperCase(), interval);
            if (marketData != null) {
                // Ensure we have valid data
                if (marketData.getClosePrice() == null) {
                    marketData.setClosePrice(
                            marketData.getOpenPrice() != null ? marketData.getOpenPrice() : BigDecimal.ZERO);
                }
                return ResponseEntity.ok(marketData);
            } else {
                // Return a default MarketData object with basic info
                MarketData defaultData = new MarketData();
                defaultData.setClosePrice(BigDecimal.ZERO);
                defaultData.setOpenPrice(BigDecimal.ZERO);
                return ResponseEntity.ok(defaultData);
            }
        } catch (Exception e) {
            // Return a safe default response
            MarketData errorData = new MarketData();
            errorData.setClosePrice(BigDecimal.valueOf(-1)); // Indicate error
            return ResponseEntity.ok(errorData);
        }
    }

    /**
     * REST API endpoint to fetch intraday data for a symbol.
     * Returns all available intraday data points.
     */
    @GetMapping("/api/intraday/{symbol}")
    @ResponseBody
    public ResponseEntity<List<MarketData>> getIntradayData(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "5min") String interval) {

        try {
            List<MarketData> marketDataList = marketDataService.fetchIntradayDataFromApiSync(symbol.toUpperCase(),
                    interval);
            return ResponseEntity.ok(marketDataList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * REST API endpoint to clear cache for a specific symbol.
     */
    @PostMapping("/api/clear-cache/{symbol}")
    @ResponseBody
    public ResponseEntity<String> clearCache(@PathVariable String symbol) {
        try {
            marketDataService.clearCacheForSymbol(symbol.toUpperCase());
            String message = String.format("Cache cleared for symbol %s", symbol.toUpperCase());
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to clear cache for symbol %s: %s",
                    symbol.toUpperCase(), e.getMessage());
            return ResponseEntity.badRequest().body(errorMessage);
        }
    }

    /**
     * REST API endpoint to get stock data by ID.
     * Returns stock information for AJAX operations.
     */
    @GetMapping("/api/stock/{stockId}")
    @ResponseBody
    public ResponseEntity<Stock> getStockById(@PathVariable Long stockId) {
        try {
            // Get stock from repository using the existing getStockBySymbol method pattern
            // Since we need to get by ID, we'll need to modify the approach
            // For now, let's use a simpler approach that works with existing methods

            // Get all active stocks and find the one with matching ID
            List<Stock> allStocks = stockService.getActiveStocks();
            Stock targetStock = allStocks.stream()
                    .filter(stock -> stock.getId().equals(stockId))
                    .findFirst()
                    .orElse(null);

            if (targetStock != null) {
                return ResponseEntity.ok(targetStock);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
