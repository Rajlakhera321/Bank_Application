package bank.web.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import bank.web.app.entity.Transactions;

public interface TransactionRepository extends JpaRepository<Transactions, Long> {

    Page<Transactions> findByOwner_Uid(String uid, Pageable pageable);

    Page<Transactions> findAllByCard_CardIdAndOwner_Uid(String cardId, String uid, Pageable pageable);

    Page<Transactions> findAllByAccount_AccountIdAndOwner_Uid(String accountId, String uid, Pageable pageable);

}
