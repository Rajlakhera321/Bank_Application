package bank.web.app.helper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import bank.web.app.dto.AccountDto;
import bank.web.app.entity.Account;
import bank.web.app.entity.Status;
import bank.web.app.entity.Transactions;
import bank.web.app.entity.Type;
import bank.web.app.entity.User;
import bank.web.app.repository.AccountRepository;
import bank.web.app.repository.TransactionRepository;
import bank.web.app.util.RandomUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Getter
public class AccountHelper {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private final Map<String, String> CURRENCIES = Map.of(
            "USD", "United States Dollar",
            "EUR", "Euro",
            "GBP", "British Pound",
            "JPY", "Japanese Yen",
            "NGN", "Nigerian Naira",
            "INR", "Indian Rupee"
    );

    public Account createAccount(AccountDto accountDto, User user) throws Exception {
        long accountNumber;

        validateAccountNonExistsForUser(accountDto.getCode(), user.getUid());

        do {
            accountNumber = new RandomUtil().generateRandomLong(10);
        } while (accountRepository.existsByAccountNumber(accountNumber));

        var account = Account.builder()
                .accountNumber(accountNumber)
                .accountName(user.getFirstname() + " " + user.getLastname())
                .balance(1000)
                .owner(user)
                .code(accountDto.getCode())
                .symbol(accountDto.getSymbol())
                .label(CURRENCIES.get(accountDto.getCode()))
                .build();

        return accountRepository.save(account);
    }

    public Transactions performTransfer(Account senderAccount, Account receiverAccount, double amount, User user) throws Exception {
        validateSufficientFunds(senderAccount, (amount * 1.01));
        senderAccount.setBalance(senderAccount.getBalance() - (amount * 1.01));
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);
        accountRepository.saveAll(List.of(senderAccount, receiverAccount));
        var senderTransactions = Transactions.builder()
                .account(senderAccount)
                .status(Status.COMPLETED)
                .type(Type.WITHDRAWAL)
                .txFee(amount * 0.01)
                .amount(amount)
                .owner(senderAccount.getOwner())
                .build();

        var receiverTransactions = Transactions.builder()
                .account(receiverAccount)
                .status(Status.COMPLETED)
                .type(Type.DEPOSIT)
                .amount(amount)
                .owner(receiverAccount.getOwner())
                .build();

        return transactionRepository.saveAll(List.of(senderTransactions, receiverTransactions)).get(0);
    }

    private void validateAccountNonExistsForUser(String code, String uid) throws Exception {
        if (accountRepository.existsByCodeAndOwner_Uid(code, uid)) {
            throw new Exception("Account of this type already exists for this user.");
        }
    }

    public void validateAccountOwnership(Account account, String uid) throws Exception {
        if (!account.getOwner().getUid().equals(uid)) {
            throw new Exception("User does not own this account.");
        }
    }

    public void validateSufficientFunds(Account account, double amount) throws Exception {
        if (account.getBalance() < amount) {
            throw new Exception("Insufficient funds in the account.");
        }
    }
}
