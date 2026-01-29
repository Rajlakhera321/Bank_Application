package bank.web.app.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bank.web.app.entity.Card;
import bank.web.app.entity.Status;
import bank.web.app.entity.Transactions;
import bank.web.app.entity.Type;
import bank.web.app.entity.User;
import bank.web.app.helper.AccountHelper;
import bank.web.app.repository.AccountRepository;
import bank.web.app.repository.CardRepository;
import bank.web.app.repository.TransactionRepository;
import bank.web.app.util.RandomUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {

    private final CardRepository cardRepository;
    private final AccountHelper accountHelper;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public Card getCard(User user) {
        return cardRepository.findByOwner_Uid(user.getUid()).orElseThrow();
    }

    public Card createCard(double amount, User user) throws Exception {
        System.out.println("Creating card with amount: " + amount);
        if (amount < 2) {
            throw new Exception("Amount should be at least $2");
        }

        if (!accountRepository.existsByCodeAndOwner_Uid("USD", user.getUid())) {
            throw new Exception("USD account not found for this user so cannot create card");
        }

        var usdAccount = accountRepository.findByCodeAndOwner_Uid("USD", user.getUid()).orElseThrow();

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
                .cvv(new RandomUtil().generateRandomLong(3).toString())
                .balance(amount - 1)
                .build();

        accountHelper.createAccountTransaction(1, Type.WITHDRAWAL, 0.00, user, usdAccount);
        accountHelper.createAccountTransaction(amount - 1, Type.WITHDRAWAL, 0.00, user, usdAccount);
        createCardTransaction(amount, Type.CREDIT, 0.00, user, card);

        accountRepository.save(usdAccount);
        return cardRepository.save(card);
    }

    private long generateCardNumber() {
        return new RandomUtil().generateRandomLong(16);
    }

    public Transactions creditCard(double amount, User user) {
        var usdAccount = accountRepository.findByCodeAndOwner_Uid("USD", user.getUid()).orElseThrow();
        usdAccount.setBalance(usdAccount.getBalance() - amount);
        accountHelper.createAccountTransaction(amount, Type.WITHDRAWAL, 0.00, user, usdAccount);
        var card = user.getCard();
        card.setBalance(card.getBalance() + amount);
        createCardTransaction(amount, Type.CREDIT, 0.00, user, card);
        return createCardTransaction(amount, Type.CREDIT, 0.00, user, card);
    }

    public Transactions debitCard(double amount, User user) {
        var usdAccount = accountRepository.findByCodeAndOwner_Uid("USD", user.getUid()).orElseThrow();
        usdAccount.setBalance(usdAccount.getBalance() + amount);
        accountHelper.createAccountTransaction(amount, Type.DEPOSIT, 0.00, user, usdAccount);
        var card = user.getCard();
        card.setBalance(card.getBalance() - amount);
        createCardTransaction(amount, Type.DEBIT, 0.00, user, card);
        return createCardTransaction(amount, Type.DEBIT, 0.00, user, card);
    }

    private Transactions createCardTransaction(double amount, Type type, double txFee, User user, Card card) {
        var tx = Transactions.builder()
                .amount(amount)
                .type(type)
                .txFee(txFee)
                .status(Status.COMPLETED)
                .card(card)
                .owner(user)
                .build();

        return transactionRepository.save(tx);
    }

}
