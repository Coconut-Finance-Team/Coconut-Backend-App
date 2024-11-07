package com.coconut.stock_app.entity.on_premise;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    private String accountId;

    @Column(unique = true, nullable = false, length = 36)
    private String accountUuid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountsAccountStatusEnum accountStatus;

    private String accountAlias;

    @Column(nullable = false, length = 255)
    private String accountPassword;

    private String accountPurpose;

    private BigDecimal withdrawableAmount;

    private BigDecimal deposit;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OwnedStock> ownedStocks;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProfitLoss> profitLosses;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;
}

enum AccountsAccountStatusEnum {
    OPEN,
    CLOSED
}
