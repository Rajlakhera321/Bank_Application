package bank.web.app.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import bank.web.app.entity.Account;
import bank.web.app.entity.Card;
import bank.web.app.entity.Status;
import bank.web.app.entity.Transactions;
import bank.web.app.entity.Type;
import bank.web.app.entity.User;
import bank.web.app.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<Transactions> getAllTransactions(String page, User user) {
        Pageable pageable = PageRequest.of(Integer.parseInt(page), 10, Sort.by("createdAt").ascending());

        return transactionRepository.findByOwner_Uid(user.getUid(), pageable).getContent();
    }

    public List<Transactions> getTransactionsByCardId(String cardId, String page, User user) {
        Pageable pageable = PageRequest.of(Integer.parseInt(page), 10, Sort.by("createdAt").ascending());

        return transactionRepository.findAllByCard_CardIdAndOwner_Uid(cardId, user.getUid(), pageable).getContent();
    }

    public List<Transactions> getTransactionsByAccountId(String accountId, String page, User user) {
        Pageable pageable = PageRequest.of(Integer.parseInt(page), 10, Sort.by("createdAt").ascending());

        return transactionRepository.findAllByAccount_AccountIdAndOwner_Uid(accountId, user.getUid(), pageable).getContent();
    }

    public Transactions createAccountTransaction(double amount, Type type, double txFee, User user, Account usdAccount) {
        var tx = Transactions.builder()
                .amount(amount)
                .type(type)
                .txFee(txFee)
                .status(Status.COMPLETED)
                .account(usdAccount)
                .owner(user)
                .build();

        return transactionRepository.save(tx);
    }

    public Transactions createCardTransaction(double amount, Type type, double txFee, User user, Card card) {
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
