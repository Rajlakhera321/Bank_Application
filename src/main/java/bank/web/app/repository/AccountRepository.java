package bank.web.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import bank.web.app.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByAccountNumber(long accountNumber);

    boolean existsByCodeAndOwner_Uid(String code, String uid);

    List<Account> findByOwner_Uid(String uid);

    Optional<Account> findByCodeAndOwner_Uid(String uid);

    Optional<Account> findByAccountNumber(String recipientAccountNumber);
}
