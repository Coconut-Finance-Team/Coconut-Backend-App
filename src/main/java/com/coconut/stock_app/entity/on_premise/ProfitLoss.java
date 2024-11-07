package com.coconut.stock_app.entity.on_premise;

import com.coconut.stock_app.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "profit_loss")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfitLoss extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profitLossId;

    @Column(nullable = false, length = 20)
    private String stockCode;

    @Column(nullable = false, length = 100)
    private String stockName;

    @Column(nullable = false)
    private BigDecimal purchasePricePerShare;

    @Column(nullable = false)
    private BigDecimal salePricePerShare;

    private BigDecimal profitRate;

    @Column(nullable = false)
    private BigDecimal fee;

    @Column(nullable = false)
    private int saleQuantity;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "accountId", nullable = false)
    private Account account;
}
