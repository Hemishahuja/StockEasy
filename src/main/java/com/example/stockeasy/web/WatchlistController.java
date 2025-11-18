package com.example.stockeasy.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    
    @Autowired(required = false)
    private WatchlistService watchlistService;

    // In-memory list just for demo / UI. This resets when the app restarts.
    private final List<WatchlistItem> watchlist = new ArrayList<>();

    public WatchlistController() {
        // Demo rows so the table is never empty
        watchlist.add(new WatchlistItem("AAPL", new BigDecimal("185.32"), "Apple demo stock"));
        watchlist.add(new WatchlistItem("GOOGL", new BigDecimal("132.10"), "Google demo stock"));
        watchlist.add(new WatchlistItem("MSFT", new BigDecimal("410.00"), "Microsoft demo stock"));
    }
    
    /**
     * Display user's watchlist
     */
    @GetMapping
    public String getWatchlist(Model model) {
        model.addAttribute("watchlist", watchlist);
        // backing object for the modal form (symbol, price, notes)
        model.addAttribute("newItem", new WatchlistItem());
        return "watchlist/index";   // matches templates/watchlist/index.html
    }

    /**
     * Simple DTO just for the watchlist page / form.
     */
    public static class WatchlistItem {
        private String symbol;
        private BigDecimal price;
        private String notes;

        public WatchlistItem() {
        }

        public WatchlistItem(String symbol, BigDecimal price, String notes) {
            this.symbol = symbol;
            this.price = price;
            this.notes = notes;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    /**
     * Add stock to watchlist (called from the modal form: symbol, price, notes)
     */
    @PostMapping("/add")
    public String addToWatchlist(@ModelAttribute("newItem") WatchlistItem formItem, Model model) {
        try {
            if (formItem.getSymbol() != null && !formItem.getSymbol().isBlank()) {
                if (formItem.getPrice() == null) {
                    formItem.setPrice(BigDecimal.ZERO);
                }

                // Add to in-memory list so it appears in the table
                watchlist.add(formItem);

                // OPTIONAL: later you can also connect to service here if you want
                // if (watchlistService != null) {
                //     watchlistService.addToWatchlist(userId, stockIdFromSymbol, ...);
                // }

                model.addAttribute("message", "Stock added to watchlist successfully!");
            } else {
                model.addAttribute("error", "Symbol is required.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add stock to watchlist: " + e.getMessage());
        }

        // Redirect to refresh the list
        return "redirect:/watchlist";
    }
    
    /**
     * Remove stock from watchlist
     * (currently only calls the service; does NOT update the in-memory list).
     * You can hook this up properly later.
     */
    @PostMapping("/remove/{watchlistId}")
    public String removeFromWatchlist(@PathVariable Long watchlistId, Model model) {
        try {
            if (watchlistService != null) {
                watchlistService.removeFromWatchlist(watchlistId);
            }
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
            if (watchlistService != null) {
                watchlistService.enableAlerts(watchlistId);
            }
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
            if (watchlistService != null) {
                watchlistService.disableAlerts(watchlistId);
            }
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
    public String setAlertPrice(@PathVariable Long watchlistId,
                                @RequestParam BigDecimal alertPrice,
                                Model model) {
        try {
            if (watchlistService != null) {
                watchlistService.setAlertPrice(watchlistId, alertPrice);
            }
            model.addAttribute("message", "Alert price set successfully!");
            return "redirect:/watchlist";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to set alert price: " + e.getMessage());
            return "watchlist/error";
        }
    }
}
