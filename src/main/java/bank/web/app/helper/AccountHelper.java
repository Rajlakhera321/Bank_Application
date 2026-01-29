package bank.web.app.helper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import bank.web.app.dto.AccountDto;
import bank.web.app.dto.ConvertDto;
import bank.web.app.entity.Account;
import bank.web.app.entity.Status;
import bank.web.app.entity.Transactions;
import bank.web.app.entity.Type;
import bank.web.app.entity.User;
import bank.web.app.repository.AccountRepository;
import bank.web.app.repository.TransactionRepository;
import bank.web.app.service.ExchangeRateService;
import bank.web.app.util.RandomUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Getter
public class AccountHelper {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ExchangeRateService exchangeRateService;

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
        var senderTransactions = createAccountTransaction(amount, Type.WITHDRAWAL, amount * 0.01, user, senderAccount);
        var receiverTransactions = createAccountTransaction(amount, Type.DEPOSIT, 0.00, receiverAccount.getOwner(), receiverAccount);
        return senderTransactions;
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

    public void validateAmount(double amount) throws Exception {
        if (amount <= 0) {
            throw new Exception("Amount must be greater than zero.");
        }
    }

    public void validateDifferentCurrencyType(ConvertDto convertDto) throws Exception {
        if (convertDto.getToCurrency().equalsIgnoreCase(convertDto.getFromCurrency())) {
            throw new Exception("Conversion between the same currency types is not allowed.");
        }
    }

    public void validateAccountOwnership(ConvertDto convertDto, String uid) throws Exception {
        accountRepository.findByCodeAndOwner_Uid(convertDto.getFromCurrency(), uid)
                .orElseThrow(() -> new Exception("Account of type " + convertDto.getFromCurrency() + " does not exist for from user."));
        accountRepository.findByCodeAndOwner_Uid(convertDto.getToCurrency(), uid)
                .orElseThrow(() -> new Exception("Account of type " + convertDto.getToCurrency() + " does not exist for to user."));
    }

    public void validateConversion(ConvertDto convertDto, String uid) throws Exception {
        validateDifferentCurrencyType(convertDto);
        validateAccountOwnership(convertDto, uid);
        validateAmount(convertDto.getAmount());
        validateSufficientFunds(
                accountRepository.findByCodeAndOwner_Uid(convertDto.getFromCurrency(), uid).get(),
                convertDto.getAmount()
        );
    }

    public Transactions convertCurrency(ConvertDto convertDto, User user) throws Exception {
        validateConversion(convertDto, user.getUid());
        var rates = exchangeRateService.getRates();
        var sendingRates = rates.get(convertDto.getFromCurrency());
        var receivingRates = rates.get(convertDto.getToCurrency());
        var computedAmount = (receivingRates / sendingRates) * convertDto.getAmount();
        var fromAccount = accountRepository.findByCodeAndOwner_Uid(convertDto.getFromCurrency(), user.getUid()).get();
        var toAccount = accountRepository.findByCodeAndOwner_Uid(convertDto.getToCurrency(), user.getUid()).get();
        fromAccount.setBalance(fromAccount.getBalance() - (convertDto.getAmount() * 1.01));
        toAccount.setBalance(toAccount.getBalance() + computedAmount);
        accountRepository.saveAll(List.of(fromAccount, toAccount));

        var fromAccounttransaction = createAccountTransaction(convertDto.getAmount(), Type.CONVERSION, convertDto.getAmount() * 0.01, user, fromAccount);
        var toAccounttransaction = createAccountTransaction(computedAmount, Type.DEPOSIT, 0.00, user, toAccount);

        return fromAccounttransaction;
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
}
