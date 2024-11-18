package com.coconut.stock_app.entity.on_premise;

import com.coconut.stock_app.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "owned_stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnedStock extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String stockCode;

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    private BigDecimal totalPurchasePrice;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "accountId")
    private Account account;

    public void buyStock(Long bQuantity, BigDecimal bPrice) {
        this.quantity += bQuantity;
        this.totalPurchasePrice.add(bPrice.multiply(new BigDecimal(bQuantity)));
    }

    public void sellStock(Long sQuantity) {
        if (sQuantity > this.quantity) {
            throw new IllegalArgumentException("보유한 주식 수량이 부족합니다.");
        }


        BigDecimal averagePricePerShare = this.totalPurchasePrice.divide(new BigDecimal(this.quantity), BigDecimal.ROUND_HALF_UP);

        BigDecimal amountToDeduct = averagePricePerShare.multiply(new BigDecimal(sQuantity));
        this.totalPurchasePrice = this.totalPurchasePrice.subtract(amountToDeduct);

        this.quantity -= sQuantity;
    }
}
