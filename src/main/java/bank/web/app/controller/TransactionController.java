package bank.web.app.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bank.web.app.entity.Transactions;
import bank.web.app.entity.User;
import bank.web.app.service.TransactionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public List<Transactions> getAllTransactions(@RequestParam String page, Authentication auth) {
        User user = (User) auth.getPrincipal();
        return transactionService.getAllTransactions(page, user);
    }

    @GetMapping("/c/{cardId}")
    public List<Transactions> getTransactionsByCardId(@PathVariable String cardId, @RequestParam String page, Authentication auth) {
        User user = (User) auth.getPrincipal();
        return transactionService.getTransactionsByCardId(cardId, page, user);
    }

    @GetMapping("/a/{accountId}")
    public List<Transactions> getTransactionsByAccountId(@PathVariable String accountId, @RequestParam String page, Authentication auth) {
        User user = (User) auth.getPrincipal();
        return transactionService.getTransactionsByAccountId(accountId, page, user);
    }
}
