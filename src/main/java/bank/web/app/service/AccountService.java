package bank.web.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import bank.web.app.dto.AccountDto;
import bank.web.app.entity.Account;
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
}
