package com.coconut.stock_app.entity.cloud;

import com.coconut.stock_app.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "stock_charts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockChart extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chartId;

    @Column(nullable = false)
    private LocalDateTime tradeDate;

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

    @ManyToOne
    @JoinColumn(name = "stock_code", referencedColumnName = "stockCode", nullable = false)
    private Stock stock;

}

