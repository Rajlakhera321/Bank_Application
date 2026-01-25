package bank.web.app.service;

import java.util.List;

import org.jspecify.annotations.Nullable;
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

    public Account createAccount(AccountDto accountDto, User user) throws Exception {
        return accountHelper.createAccount(accountDto, user);
    }

    public List<Account> getAccountsByUser(String uid) {
        return accountRepository.findByOwner_Uid(uid);
    }

    public Transactions transferAccount(TransferDto transferDto, User user) {
        var senderAccount = accountRepository.findByCodeAndOwner_Uid(user.getUid())
                .orElseThrow(() -> new UnsupportedOperationException("Account of type currency do not exists for user"));
        var receiverAccount = accountRepository.findByAccountNumber(transferDto.getRecipientAccountNumber());
    }
}
