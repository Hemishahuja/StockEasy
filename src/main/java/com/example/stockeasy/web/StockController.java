package com.example.stockeasy.web;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.stockeasy.domain.Stock;
import com.example.stockeasy.service.StockService;

/**
 * StockController for stock management.
 * Handles stock search, display, and operations.
 */
@Controller
@RequestMapping("/stocks")
public class StockController {
    
    @Autowired
    private StockService stockService;
    
    /**
     * Display all active stocks
     */
    @GetMapping
    public String getAllStocks(Model model) {
        List<Stock> stocks = stockService.getActiveStocks();
        model.addAttribute("stocks", stocks);
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
        Stock stock = stockService.getStockBySymbol("AAPL"); // For demo, use a default stock
        model.addAttribute("stock", stock);
        return "stocks/detail";
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
}
