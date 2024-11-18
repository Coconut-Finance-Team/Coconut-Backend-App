package com.coconut.stock_app.entity.on_premise;

import com.coconut.stock_app.entity.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends BaseEntity implements Serializable {
    @Id
    private String accountId;

    @Column(unique = true, nullable = false, length = 36)
    private String accountUuid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus accountStatus;

    @Column
    private String accountAlias;

    @Column(nullable = false, length = 255)
    private String accountPassword;

    private String accountPurpose;

    private BigDecimal withdrawableAmount;

    private BigDecimal deposit = BigDecimal.ZERO;

    private BigDecimal reservedDeposit = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "userId")
    private User user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OwnedStock> ownedStocks = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProfitLoss> profitLosses;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;

    public void increaseBalance(BigDecimal amount){
        this.deposit = this.deposit.add(amount);
    }

    public void decreaseBalance(BigDecimal amount){
        this.deposit = this.deposit.subtract(amount);
        this.reservedDeposit = this.reservedDeposit.subtract(amount);
    }

    public void increaseReservedDeposit(BigDecimal amount){
        this.reservedDeposit = this.reservedDeposit.add(amount);
    }
}

