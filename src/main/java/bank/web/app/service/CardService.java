package bank.web.app.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bank.web.app.entity.Card;
import bank.web.app.entity.Transactions;
import bank.web.app.entity.Type;
import bank.web.app.entity.User;
import bank.web.app.helper.AccountHelper;
import bank.web.app.repository.AccountRepository;
import bank.web.app.repository.CardRepository;
import bank.web.app.util.RandomUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {

    private final CardRepository cardRepository;
    private final AccountHelper accountHelper;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    public Card getCard(User user) {
        return cardRepository.findByOwner_Uid(user.getUid()).orElseThrow();
    }

    public Card createCard(double amount, User user) throws Exception {
        System.out.println("Creating card with amount: " + amount);
        if (amount < 2) {
            throw new Exception("Amount should be at least $2");
        }

        if (!accountHelper.existsByCodeAndOwnerUid("USD", user.getUid())) {
            throw new Exception("USD account not found for this user so cannot create card");
        }

        var usdAccount = accountHelper.findByCodeAndOwnerUid("USD", user.getUid()).orElseThrow();
        accountHelper.validateSufficientFunds(usdAccount, amount);
        usdAccount.setBalance(usdAccount.getBalance() - amount);

        long cardNumber;
        do {
            cardNumber = generateCardNumber();
        } while (cardRepository.existsByCardNumber(cardNumber));

        Card card = Card.builder()
                .cardHolder(user.getFirstname() + " " + user.getLastname())
                .cardNumber(cardNumber)
                .exp(LocalDateTime.now().plusYears(3))
                .owner(user)
                .cvv(new RandomUtil().generateRandomLong(3).toString())
                .balance(amount - 1)
                .build();

        card = cardRepository.save(card);

        transactionService.createAccountTransaction(1, Type.WITHDRAWAL, 0.00, user, usdAccount);
        transactionService.createAccountTransaction(amount - 1, Type.WITHDRAWAL, 0.00, user, usdAccount);
        transactionService.createCardTransaction(amount, Type.CREDIT, 0.00, user, card);

        accountHelper.save(usdAccount);
        return card;
    }

    private long generateCardNumber() {
        return new RandomUtil().generateRandomLong(16);
    }

    public Transactions creditCard(double amount, User user) {
        var usdAccount = accountRepository.findByCodeAndOwner_Uid("USD", user.getUid()).orElseThrow();
        usdAccount.setBalance(usdAccount.getBalance() - amount);
        transactionService.createAccountTransaction(amount, Type.WITHDRAWAL, 0.00, user, usdAccount);
        var card = user.getCard();
        card.setBalance(card.getBalance() + amount);
        cardRepository.save(card);
        return transactionService.createCardTransaction(amount, Type.CREDIT, 0.00, user, card);
    }

    public Transactions debitCard(double amount, User user) {
        var usdAccount = accountRepository.findByCodeAndOwner_Uid("USD", user.getUid()).orElseThrow();
        usdAccount.setBalance(usdAccount.getBalance() + amount);
        transactionService.createAccountTransaction(amount, Type.DEPOSIT, 0.00, user, usdAccount);
        var card = getCard(user);
        card.setBalance(card.getBalance() - amount);
        cardRepository.save(card);
        return transactionService.createCardTransaction(amount, Type.DEBIT, 0.00, user, card);
    }
}
