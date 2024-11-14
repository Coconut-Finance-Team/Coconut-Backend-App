package com.coconut.stock_app.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockChartDTO {
    private String stockCode;//0
    private String time;//1
    private BigDecimal currentPrice;//2
    private BigDecimal openPrice;//7
    private BigDecimal highPrice;//8
    private BigDecimal lowPrice;//8
    private String versusSign;//3
    private BigDecimal contingentVol;//12
    private BigDecimal accumulatedVol;//13
    private BigDecimal accumulatedAmount; //14
}
