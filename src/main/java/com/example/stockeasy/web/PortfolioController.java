package com.example.stockeasy.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.stockeasy.domain.Portfolio;
import com.example.stockeasy.domain.User;
import com.example.stockeasy.service.PortfolioService;
import com.example.stockeasy.service.StockService;
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
    
    /**
     * Display user's portfolio dashboard
     */
    @GetMapping("/dashboard")
    public String getPortfolioDashboard(Model model) {
        // For demo, use user ID 1
        Long userId = 1L;
        User user = userService.getUserProfile(userId);
        List<Portfolio> portfolioItems = portfolioService.getUserPortfolio(userId);
        Double portfolioValue = portfolioService.calculatePortfolioValue(userId);
        
        model.addAttribute("user", user);
        model.addAttribute("portfolioItems", portfolioItems);
        model.addAttribute("portfolioValue", portfolioValue != null ? portfolioValue : 0.0);
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
    public String buyStock(@RequestParam Long userId, @RequestParam Long stockId, @RequestParam Integer quantity, Model model) {
        try {
            // This would normally call the transaction service
            // For demo, we'll simulate the operation
            model.addAttribute("message", "Buy order placed successfully!");
            return "redirect:/portfolio/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to place buy order: " + e.getMessage());
            return "portfolio/error";
        }
    }
    
    /**
     * Execute sell stock transaction
     */
    @PostMapping("/sell")
    public String sellStock(@RequestParam Long userId, @RequestParam Long stockId, @RequestParam Integer quantity, Model model) {
        try {
            // This would normally call the transaction service
            // For demo, we'll simulate the operation
            model.addAttribute("message", "Sell order placed successfully!");
            return "redirect:/portfolio/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to place sell order: " + e.getMessage());
            return "portfolio/error";
        }
    }
    
    /**
     * Display transaction history
     */
    @GetMapping("/history")
    public String getTransactionHistory(Model model) {
        Long userId = 1L;
        List<Portfolio> portfolioItems = portfolioService.getUserPortfolio(userId);
        
        model.addAttribute("portfolioItems", portfolioItems);
        return "portfolio/history";
    }
}
