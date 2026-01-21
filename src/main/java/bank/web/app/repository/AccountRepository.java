package bank.web.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bank.web.app.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
