package com.coconut.stock_app.entity.on_premise;

import com.coconut.stock_app.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
    private Long saleQuantity;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "accountId", nullable = false)
    private Account account;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "orderId", nullable = false, unique = true)
    private Order order;

    public void calculateProfitRate() {
        if (this.purchasePricePerShare != null && this.salePricePerShare != null) {
            BigDecimal profit = this.salePricePerShare.subtract(this.purchasePricePerShare);
            this.profitRate = profit.divide(this.purchasePricePerShare, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
    }

    public void increaseSaleQuantity(Long quantity) {
        this.saleQuantity+=quantity;
    }
}
