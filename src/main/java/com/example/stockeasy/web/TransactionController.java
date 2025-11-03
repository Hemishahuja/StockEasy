package com.example.stockeasy.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.stockeasy.domain.Transaction;
import com.example.stockeasy.service.TransactionService;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/buy")
    public String buyStock(@RequestParam Long userId,
                          @RequestParam Long stockId,
                          @RequestParam Integer quantity,
                          Model model) {
        try {
            Transaction transaction = transactionService.buyStock(userId, stockId, quantity);
            model.addAttribute("message", "Buy order executed successfully!");
            return "redirect:/portfolio/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to execute buy order: " + e.getMessage());
            return "redirect:/portfolio/dashboard";
        }
    }

    @PostMapping("/sell")
    public String sellStock(@RequestParam Long userId,
                           @RequestParam Long stockId,
                           @RequestParam Integer quantity,
                           Model model) {
        try {
            Transaction transaction = transactionService.sellStock(userId, stockId, quantity);
            model.addAttribute("message", "Sell order executed successfully!");
            return "redirect:/portfolio/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to execute sell order: " + e.getMessage());
            return "redirect:/portfolio/dashboard";
        }
    }

    @GetMapping("/history")
    public String getTransactionHistory(@RequestParam Long userId, Model model) {
        List<Transaction> transactions = transactionService.getUserTransactionHistory(userId);
        model.addAttribute("transactions", transactions);
        return "transactions/history";
    }

    @GetMapping("/recent")
    public String getRecentTransactions(@RequestParam Long userId, Model model) {
        List<Transaction> transactions = transactionService.getRecentTransactions(userId);
        model.addAttribute("transactions", transactions);
        return "transactions/recent";
    }
}
