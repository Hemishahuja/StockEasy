package com.example.stockeasy.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.stockeasy.domain.Portfolio;
import com.example.stockeasy.domain.Stock;
import com.example.stockeasy.domain.Transaction;
import com.example.stockeasy.domain.User;
import com.example.stockeasy.service.PortfolioService;
import com.example.stockeasy.service.StockService;
import com.example.stockeasy.service.TransactionService;
import com.example.stockeasy.service.UserService;

/**
 * PortfolioController for portfolio management.
 * Handles portfolio display, buy/sell operations, and portfolio analysis.
 */
@Controller
@RequestMapping("/portfolio")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private UserService userService;

    @Autowired
    private StockService stockService;

    @Autowired
    private TransactionService transactionService;

    /**
     * Display user's portfolio dashboard
     */
    @GetMapping("/dashboard")
    public String getPortfolioDashboard(Model model) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = (User) userService.loadUserByUsername(username);
            Long userId = user.getId();

            List<Portfolio> portfolioItems = portfolioService.getUserPortfolio(userId);
            Double portfolioValue = portfolioService.calculatePortfolioValue(userId);
            List<Stock> availableStocks = stockService.getActiveStocks();

            // Create simple JavaScript-compatible objects
            List<java.util.Map<String, Object>> simplePortfolioItems = portfolioItems.stream()
                .map(p -> {
                    java.util.Map<String, Object> item = new java.util.HashMap<>();
                    item.put("id", p.getId());
                    item.put("quantity", p.getQuantity());
                    item.put("averagePurchasePrice", p.getAveragePurchasePrice());
                    item.put("currentValue", p.getCurrentValue());
                    item.put("profitLoss", p.getProfitLoss());
                    item.put("profitLossPercentage", p.getProfitLossPercentage());
                    item.put("totalInvestment", p.getTotalInvestment());

                    // Add stock info
                    java.util.Map<String, Object> stockInfo = new java.util.HashMap<>();
                    if (p.getStock() != null) {
                        stockInfo.put("id", p.getStock().getId());
                        stockInfo.put("symbol", p.getStock().getSymbol());
                        stockInfo.put("companyName", p.getStock().getCompanyName());
                        stockInfo.put("currentPrice", p.getStock().getCurrentPrice());
                    }
                    item.put("stock", stockInfo);
                    return item;
                })
                .toList();

            List<java.util.Map<String, Object>> simpleAvailableStocks = availableStocks.stream()
                .map(s -> {
                    java.util.Map<String, Object> stock = new java.util.HashMap<>();
                    stock.put("id", s.getId());
                    stock.put("symbol", s.getSymbol());
                    stock.put("companyName", s.getCompanyName());
                    stock.put("currentPrice", s.getCurrentPrice());
                    return stock;
                })
                .toList();

            model.addAttribute("user", user);
            model.addAttribute("portfolioItems", portfolioItems != null ? portfolioItems : List.of());
            model.addAttribute("portfolioValue", portfolioValue != null ? portfolioValue : 0.0);
            model.addAttribute("availableStocks", availableStocks != null ? availableStocks : List.of());

            // Add simple objects for JavaScript
            model.addAttribute("simplePortfolioItems", simplePortfolioItems);
            model.addAttribute("simpleAvailableStocks", simpleAvailableStocks);

        } catch (Exception e) {
            // Provide fallback data
            model.addAttribute("user", null);
            model.addAttribute("portfolioItems", List.of());
            model.addAttribute("portfolioValue", 0.0);
            model.addAttribute("availableStocks", List.of());
            model.addAttribute("simplePortfolioItems", List.of());
            model.addAttribute("simpleAvailableStocks", List.of());
            model.addAttribute("error", "Unable to load portfolio data. Please try refreshing the page.");
        }

        return "portfolio/dashboard";
    }
    
    /**
     * Display detailed portfolio with profit/loss analysis
     */
    @GetMapping("/analysis")
    public String getPortfolioAnalysis(Model model) {
        Long userId = 1L;
        List<Portfolio> portfolioItems = portfolioService.getUserPortfolioWithProfitLossAnalysis(userId);
        
        model.addAttribute("portfolioItems", portfolioItems);
        return "portfolio/analysis";
    }
    
    /**
     * Execute buy stock transaction
     */
    @PostMapping("/buy")
    public String buyStock(@RequestParam String stockSymbol, @RequestParam Integer quantity, Model model) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = (User) userService.loadUserByUsername(username);
            Long userId = user.getId();

            // Get stock by symbol
            Stock stock = stockService.getStockBySymbol(stockSymbol.toUpperCase());

            Transaction transaction = transactionService.buyStock(userId, stock.getId(), quantity);
            model.addAttribute("message", "Buy order executed successfully! Purchased " + quantity + " shares of " + stockSymbol.toUpperCase());
            return "redirect:/portfolio/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to execute buy order: " + e.getMessage());
            return "redirect:/portfolio/dashboard";
        }
    }

    /**
     * REST API endpoint to buy stock via AJAX
     * Returns JSON response for frontend AJAX handling
     */
    @PostMapping("/api/buy")
    @org.springframework.web.bind.annotation.ResponseBody
    public org.springframework.http.ResponseEntity<java.util.Map<String, Object>> buyStockAjax(
            @org.springframework.web.bind.annotation.RequestBody java.util.Map<String, Object> request) {
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            Long stockId = ((Number) request.get("stockId")).longValue();
            String stockSymbol = (String) request.get("stockSymbol");
            Integer quantity = ((Number) request.get("quantity")).intValue();
            
            // Get stock by symbol
            Stock stock = stockService.getStockBySymbol(stockSymbol.toUpperCase());
            
            if (stock == null) {
                java.util.Map<String, Object> response = new java.util.HashMap<>();
                response.put("success", false);
                response.put("message", "Stock not found for symbol: " + stockSymbol);
                return org.springframework.http.ResponseEntity.badRequest().body(response);
            }
            
            Transaction transaction = transactionService.buyStock(userId, stock.getId(), quantity);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("message", "Stock purchase initiated successfully! Purchased " + quantity + " shares of " + stockSymbol.toUpperCase());
            
            return org.springframework.http.ResponseEntity.ok(response);
        } catch (Exception e) {
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to purchase stock: " + e.getMessage());
            
            return org.springframework.http.ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Execute sell stock transaction
     */
    @PostMapping("/sell")
    public String sellStock(@RequestParam String stockSymbol, @RequestParam Integer quantity, Model model) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = (User) userService.loadUserByUsername(username);
            Long userId = user.getId();

            // Get stock by symbol
            Stock stock = stockService.getStockBySymbol(stockSymbol.toUpperCase());

            Transaction transaction = transactionService.sellStock(userId, stock.getId(), quantity);
            model.addAttribute("message", "Sell order executed successfully! Sold " + quantity + " shares of " + stockSymbol.toUpperCase());
            return "redirect:/portfolio/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to execute sell order: " + e.getMessage());
            return "redirect:/portfolio/dashboard";
        }
    }
    
    /**
     * REST API endpoint to change user's cash balance
     * Returns JSON response for frontend AJAX handling
     */
    @PostMapping("/change-balance")
    @org.springframework.web.bind.annotation.ResponseBody
    public org.springframework.http.ResponseEntity<java.util.Map<String, Object>> changeBalance(
            @org.springframework.web.bind.annotation.RequestBody java.util.Map<String, Object> request) {
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            Double newBalance = ((Number) request.get("newBalance")).doubleValue();
            
            if (newBalance <= 0) {
                java.util.Map<String, Object> response = new java.util.HashMap<>();
                response.put("success", false);
                response.put("message", "Balance must be greater than zero.");
                return org.springframework.http.ResponseEntity.badRequest().body(response);
            }
            
            // Update user's cash balance
            User updatedUser = userService.updateUserCashBalance(userId, newBalance);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("message", "Balance updated successfully!");
            response.put("newBalance", updatedUser.getCashBalance());
            
            return org.springframework.http.ResponseEntity.ok(response);
        } catch (Exception e) {
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update balance: " + e.getMessage());
            
            return org.springframework.http.ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Display transaction history
     */
    @GetMapping("/history")
    public String getTransactionHistory(Model model) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = (User) userService.loadUserByUsername(username);
            Long userId = user.getId();

            List<Transaction> transactionHistory = transactionService.getUserTransactionHistory(userId);
            List<Portfolio> portfolioItems = portfolioService.getUserPortfolio(userId);

            model.addAttribute("user", user);
            model.addAttribute("transactionHistory", transactionHistory != null ? transactionHistory : List.of());
            model.addAttribute("portfolioItems", portfolioItems != null ? portfolioItems : List.of());

        } catch (Exception e) {
            // Provide fallback data
            model.addAttribute("user", null);
            model.addAttribute("transactionHistory", List.of());
            model.addAttribute("portfolioItems", List.of());
            model.addAttribute("error", "Unable to load transaction history. Please try refreshing the page.");
        }

        return "portfolio/history";
    }
}
