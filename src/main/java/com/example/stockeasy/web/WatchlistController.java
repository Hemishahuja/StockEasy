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
        // Temporary demo data so the page has something to show
        java.util.List<String> symbols =
                java.util.Arrays.asList("AAPL", "GOOGL", "MSFT");

        // Name must match what the HTML uses
        model.addAttribute("watchlist", symbols);

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
