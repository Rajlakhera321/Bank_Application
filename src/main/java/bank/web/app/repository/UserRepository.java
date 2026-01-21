package bank.web.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bank.web.app.entity.User;

public interface UserRepository extends JpaRepository<User, String> {

    public User findByUsernameIgnoreCase(String username);
}
