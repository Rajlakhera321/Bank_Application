package bank.web.app.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import bank.web.app.entity.Card;
import bank.web.app.entity.Transactions;
import bank.web.app.entity.User;
import bank.web.app.helper.AccountHelper;
import bank.web.app.repository.CardRepository;
import bank.web.app.util.RandomUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final AccountHelper accountHelper;

    public Card getCard(User user) {
        return cardRepository.findByOwner_Uid(user.getUid()).orElseThrow();
    }

    public Card createCard(double amount, User user) throws Exception {
        if (amount < 2) {
            throw new Exception("Amount should be at least $2");
        }

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

        return cardRepository.save(card);
    }

    private long generateCardNumber() {
        return new RandomUtil().generateRandomLong(16);
    }

    public Transactions creditCard(double amount, User user) {
        return null;
    }

    public Transactions debitCard(double amount, User user) {
        return null;
    }

}
