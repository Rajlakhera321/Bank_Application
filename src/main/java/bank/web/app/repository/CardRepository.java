package bank.web.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bank.web.app.entity.Card;

public interface CardRepository extends JpaRepository<Card, Long> {

}
