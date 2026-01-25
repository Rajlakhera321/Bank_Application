package bank.web.app.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String cardId;

    @Column(unique = true, nullable = false)
    private long cardNumber;
    private String cardHolder;
    private Double balance;

    @CreationTimestamp
    private LocalDate iss;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime exp;
    private String cvv;
    private String pin;
    private String billingAddress;

    @OneToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private User owner;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Transactions> transactions;
}
