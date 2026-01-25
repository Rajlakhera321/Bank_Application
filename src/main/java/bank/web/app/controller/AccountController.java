package bank.web.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bank.web.app.dto.AccountDto;
import bank.web.app.dto.TransferDto;
import bank.web.app.entity.Account;
import bank.web.app.entity.Transactions;
import bank.web.app.entity.User;
import bank.web.app.service.AccountService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody AccountDto accountDto, Authentication authentication) throws Exception {
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(accountService.createAccount(accountDto, user));
    }

    @GetMapping
    public ResponseEntity<List<Account>> getUserAccounts(Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(accountService.getAccountsByUser(user.getUid()));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transactions> transferFunds(@RequestBody TransferDto transferDto, Authentication authentication) throws Exception {
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(accountService.transferAccount(transferDto, user));
    }
}
