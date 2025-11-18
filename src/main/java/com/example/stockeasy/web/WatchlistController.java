package com.example.stockeasy.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.stockeasy.service.WatchlistService;

/**
 * WatchlistController for watchlist management.
 * Handles watchlist display, adding/removing stocks, and price alerts.
 */
@Controller
@RequestMapping("/watchlist")
public class WatchlistController {
    
    @Autowired
    private WatchlistService watchlistService;
    
    /**
     * Display user's watchlist
     */
    @GetMapping
    public String getWatchlist(Model model) {
        try {
            // Get current authenticated user
            org.springframework.security.core.Authentication authentication = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // For now, use a mock user ID since we don't have full authentication service
            // In a real implementation, you would get the user from UserService
            Long userId = 1L; // Temporary mock user ID
            
            // Get user's watchlist items
            java.util.List<com.example.stockeasy.domain.Watchlist> watchlistItems = 
                watchlistService.getUserWatchlist(userId);
            
            // Add to model for the template
            model.addAttribute("watchlistItems", watchlistItems);
            
            // Add user info for navbar
            model.addAttribute("user", new com.example.stockeasy.domain.User("Demo", "User", "demo@example.com"));
            
        } catch (Exception e) {
            // Fallback to empty list if there's an error
            model.addAttribute("watchlistItems", java.util.Collections.emptyList());
            model.addAttribute("user", null);
        }
        
        // This must match: templates/watchlist/index.html
        return "watchlist/index";
    }

    /**
     * Add stock to watchlist
     */
    @PostMapping("/add")
    public String addToWatchlist(@RequestParam Long userId, @RequestParam Long stockId, Model model) {
        try {
            watchlistService.addToWatchlist(userId, stockId);
            model.addAttribute("message", "Stock added to watchlist successfully!");
            return "redirect:/watchlist";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add stock to watchlist: " + e.getMessage());
            return "watchlist/error";
        }
    }

    /**
     * REST API endpoint to add stock to watchlist via AJAX
     * Returns JSON response for frontend AJAX handling
     */
    @PostMapping("/api/add")
    @org.springframework.web.bind.annotation.ResponseBody
    public org.springframework.http.ResponseEntity<java.util.Map<String, Object>> addToWatchlistAjax(
            @org.springframework.web.bind.annotation.RequestBody java.util.Map<String, Object> request) {
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            Long stockId = ((Number) request.get("stockId")).longValue();
            
            watchlistService.addToWatchlist(userId, stockId);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("message", "Stock added to watchlist successfully!");
            
            return org.springframework.http.ResponseEntity.ok(response);
        } catch (Exception e) {
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to add stock to watchlist: " + e.getMessage());
            
            return org.springframework.http.ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Remove stock from watchlist
     */
    @PostMapping("/remove/{watchlistId}")
    public String removeFromWatchlist(@PathVariable Long watchlistId, Model model) {
        try {
            watchlistService.removeFromWatchlist(watchlistId);
            model.addAttribute("message", "Stock removed from watchlist successfully!");
            return "redirect:/watchlist";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to remove stock from watchlist: " + e.getMessage());
            return "watchlist/error";
        }
    }
    
    /**
     * Enable price alerts for a watchlist item
     */
    @PostMapping("/enable-alerts/{watchlistId}")
    public String enableAlerts(@PathVariable Long watchlistId, Model model) {
        try {
            watchlistService.enableAlerts(watchlistId);
            model.addAttribute("message", "Price alerts enabled successfully!");
            return "redirect:/watchlist";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to enable price alerts: " + e.getMessage());
            return "watchlist/error";
        }
    }
    
    /**
     * Disable price alerts for a watchlist item
     */
    @PostMapping("/disable-alerts/{watchlistId}")
    public String disableAlerts(@PathVariable Long watchlistId, Model model) {
        try {
            watchlistService.disableAlerts(watchlistId);
            model.addAttribute("message", "Price alerts disabled successfully!");
            return "redirect:/watchlist";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to disable price alerts: " + e.getMessage());
            return "watchlist/error";
        }
    }
    
    /**
     * Set alert price for a watchlist item
     */
    @PostMapping("/set-alert-price/{watchlistId}")
    public String setAlertPrice(@PathVariable Long watchlistId, @RequestParam java.math.BigDecimal alertPrice, Model model) {
        try {
            watchlistService.setAlertPrice(watchlistId, alertPrice);
            model.addAttribute("message", "Alert price set successfully!");
            return "redirect:/watchlist";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to set alert price: " + e.getMessage());
            return "watchlist/error";
        }
    }
}
