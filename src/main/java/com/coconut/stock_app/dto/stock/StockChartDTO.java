package com.coconut.stock_app.dto.stock;

import com.coconut.stock_app.entity.cloud.Stock;
import com.coconut.stock_app.entity.cloud.StockChart;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockChartDTO {
    private String stockCode;
    private BigDecimal currentPrice;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private String versusSign;
    private BigDecimal contingentVol;
    private BigDecimal accumulatedVol;
    private BigDecimal accumulatedAmount;
    private String time;

    public StockChart toEntity(Stock stock) {
        return StockChart.builder()
                .currentPrice(this.currentPrice)
                .openPrice(this.openPrice)
                .highPrice(this.highPrice)
                .lowPrice(this.lowPrice)
                .versusSign(this.versusSign)
                .contingentVol(this.contingentVol)
                .accumulatedVol(this.accumulatedVol)
                .accumulatedAmount(this.accumulatedAmount)
                .time(LocalDateTime.parse(this.time))
                .stock(stock)
                .build();
    }
}
