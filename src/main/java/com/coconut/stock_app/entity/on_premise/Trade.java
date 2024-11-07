package com.coconut.stock_app.entity.on_premise;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "trades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tradeId;

    @Column(nullable = false)
    private LocalDate tradeDate;

    @Column(nullable = false)
    private int tradeQuantity;

    @Column(nullable = false)
    private BigDecimal tradePrice;

    @Column(nullable = false)
    private BigDecimal fee = BigDecimal.valueOf(10.00);

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "buy_order_id", referencedColumnName = "orderId", nullable = false)
    private Order buyOrder;

    @ManyToOne
    @JoinColumn(name = "sell_order_id", referencedColumnName = "orderId", nullable = false)
    private Order sellOrder;
}
