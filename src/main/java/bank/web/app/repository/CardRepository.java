package bank.web.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import bank.web.app.entity.Card;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByOwner_Uid(String uid);

    boolean existsByCardNumber(long cardNumber);

}
