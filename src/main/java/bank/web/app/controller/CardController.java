package bank.web.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bank.web.app.entity.Card;
import bank.web.app.entity.Transactions;
import bank.web.app.entity.User;
import bank.web.app.service.CardService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping
    public ResponseEntity<Card> getCards(Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(cardService.getCard(user));
    }

    @PostMapping("create")
    public ResponseEntity<Card> createCard(@RequestParam double amount, Authentication authentication) throws Exception {
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(cardService.createCard(amount, user));
    }

    @PostMapping("credit")
    public ResponseEntity<Transactions> creditCard(@RequestParam double amount, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(cardService.creditCard(amount, user));
    }

    @PostMapping("debit")
    public ResponseEntity<Transactions> debitCard(@RequestParam double amount, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(cardService.debitCard(amount, user));
    }
}
