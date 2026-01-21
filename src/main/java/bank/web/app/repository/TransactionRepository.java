package bank.web.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bank.web.app.entity.Transactions;

public interface TransactionRepository extends JpaRepository<Transactions, Long> {

}
