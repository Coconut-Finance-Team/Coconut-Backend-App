package com.coconut.stock_app.entity.cloud;

import com.coconut.stock_app.entity.common.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

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

    private BigDecimal openPrice;

    private BigDecimal highPrice;

    private BigDecimal lowPrice;

    private BigDecimal currentPrice;

    private String versusSign;

    private BigDecimal contingentVol;

    private BigDecimal accumulatedVol;

    private BigDecimal accumulatedAmount;

    @Column(name = "time")
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name = "stock_code", referencedColumnName = "stockCode", nullable = false)
    private Stock stock;

}
