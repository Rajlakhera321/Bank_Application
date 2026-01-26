package bank.web.app.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import bank.web.app.dto.AccountDto;
import bank.web.app.dto.TransferDto;
import bank.web.app.entity.Account;
import bank.web.app.entity.Transactions;
import bank.web.app.entity.User;
import bank.web.app.helper.AccountHelper;
import bank.web.app.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountHelper accountHelper;
    private final ExchangeRateService exchangeRateService;

    public Account createAccount(AccountDto accountDto, User user) throws Exception {
        return accountHelper.createAccount(accountDto, user);
    }

    public List<Account> getAccountsByUser(String uid) {
        return accountRepository.findByOwner_Uid(uid);
    }

    public Transactions transferAccount(TransferDto transferDto, User user) throws Exception {
        var senderAccount = accountRepository.findByCodeAndOwner_Uid(transferDto.getCode(), user.getUid())
                .orElseThrow(() -> new UnsupportedOperationException("Account of type currency do not exists for user"));
        var receiverAccount = accountRepository.findByAccountNumber(transferDto.getRecipientAccountNumber()).orElseThrow(() -> new Exception("Recipient account does not exist."));
        return accountHelper.performTransfer(senderAccount, receiverAccount, transferDto.getAmount(), user);
    }

    public Map<String, Double> getExchangeRate() {
        return exchangeRateService.getRates();
    }
}
