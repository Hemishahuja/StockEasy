package com.example.stockeasy.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.stockeasy.service.StockService;
import com.example.stockeasy.service.UserService;

/**
 * DashboardController for the main dashboard.
 * Handles dashboard display with market overview and user information.
 */
@Controller
public class DashboardController {
    
    @Autowired
    private StockService stockService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Display main dashboard
     */
    @GetMapping("/")
    public String getDashboard(Model model) {
        try {
            // Get market overview
            List<Object[]> marketData = stockService.getStocksWithLatestMarketData();
            
            // For demo, create some sample data
            List<com.example.stockeasy.domain.Stock> activeStocks = stockService.getActiveStocks();
            
            // Get user info (for demo, use user ID 1)
            com.example.stockeasy.domain.User user = userService.getUserProfile(1L);
            
            // Calculate portfolio value
            java.math.BigDecimal portfolioValue = userService.getPortfolioValue(1L);
            
            model.addAttribute("user", user);
            model.addAttribute("activeStocks", activeStocks);
            model.addAttribute("marketData", marketData);
            model.addAttribute("portfolioValue", portfolioValue != null ? portfolioValue : java.math.BigDecimal.ZERO);
            
        } catch (Exception e) {
            // Add some default data for demo purposes
            model.addAttribute("message", "Demo mode - Using sample data");
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
    
    /**
     * Display login page
     */
    @GetMapping("/login")
    public String getLoginPage() {
        return "auth/login";
    }
    
    /**
     * Display register page
     */
    @GetMapping("/register")
    public String getRegisterPage() {
        return "auth/register";
    }
}
