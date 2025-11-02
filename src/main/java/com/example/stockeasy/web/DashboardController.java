package com.example.stockeasy.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.stockeasy.domain.Portfolio;
import com.example.stockeasy.domain.Transaction;
import com.example.stockeasy.service.PortfolioService;
import com.example.stockeasy.service.StockService;
import com.example.stockeasy.service.TransactionService;
import com.example.stockeasy.service.UserService;

/**
 * DashboardController for the main dashboard.
 * Handles dashboard display with market overview, portfolio summary, and recent activity.
 */
@Controller
public class DashboardController {

    @Autowired
    private StockService stockService;

    @Autowired
    private UserService userService;

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private TransactionService transactionService;

    /**
     * Display main dashboard with comprehensive data
     */
    @GetMapping("/")
    public String getDashboard(Model model) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            com.example.stockeasy.domain.User user = (com.example.stockeasy.domain.User) userService.loadUserByUsername(username);
            Long userId = user.getId();

            // Get active stocks for market overview
            List<com.example.stockeasy.domain.Stock> activeStocks = stockService.getActiveStocks();

            // Calculate portfolio value and get portfolio data
            java.math.BigDecimal portfolioValue = userService.getPortfolioValue(userId);
            List<Portfolio> userPortfolio = portfolioService.getUserPortfolio(userId);

            // Get recent transactions for activity feed
            List<Transaction> recentTransactions = transactionService.getRecentTransactions(userId);

            // Calculate portfolio performance metrics
            Double totalPortfolioValue = portfolioService.calculatePortfolioValue(userId);
            java.math.BigDecimal totalValue = totalPortfolioValue != null ?
                java.math.BigDecimal.valueOf(totalPortfolioValue) : java.math.BigDecimal.ZERO;

            // Add all data to model
            model.addAttribute("user", user);
            model.addAttribute("activeStocks", activeStocks != null ? activeStocks : List.of());
            model.addAttribute("portfolioValue", portfolioValue != null ? portfolioValue : java.math.BigDecimal.ZERO);
            model.addAttribute("totalPortfolioValue", totalValue);
            model.addAttribute("userPortfolio", userPortfolio != null ? userPortfolio : List.of());
            model.addAttribute("recentTransactions", recentTransactions != null ? recentTransactions : List.of());
            model.addAttribute("stocksTracked", activeStocks != null ? activeStocks.size() : 0);

        } catch (Exception e) {
            // Log the error and provide fallback data
            System.err.println("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();

            // Add minimal fallback data
            model.addAttribute("user", null);
            model.addAttribute("activeStocks", List.of());
            model.addAttribute("portfolioValue", java.math.BigDecimal.ZERO);
            model.addAttribute("totalPortfolioValue", java.math.BigDecimal.ZERO);
            model.addAttribute("userPortfolio", List.of());
            model.addAttribute("recentTransactions", List.of());
            model.addAttribute("stocksTracked", 0);
            model.addAttribute("error", "Unable to load some dashboard data. Please try refreshing the page.");
        }

        return "dashboard/index";
    }
    
    /**
     * Display market overview
     */
    @GetMapping("/market")
    public String getMarketOverview(Model model) {
        List<com.example.stockeasy.domain.Stock> stocks = stockService.getActiveStocks();
        model.addAttribute("stocks", stocks);
        return "market/overview";
    }
}
