package com.coconut.stock_app.entity.cloud;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_charts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockChart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chartId;

    @Column(nullable = false)
    private LocalDate tradeDate;

    @Column(nullable = false)
    private BigDecimal openPrice;

    @Column(nullable = false)
    private BigDecimal highPrice;

    @Column(nullable = false)
    private BigDecimal lowPrice;

    @Column(nullable = false)
    private BigDecimal closePrice;

    @Column(nullable = false)
    private Long volume;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "stock_code", referencedColumnName = "stockCode", nullable = false)
    private Stock stock;
}

