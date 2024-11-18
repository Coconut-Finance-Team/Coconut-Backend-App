package com.coconut.stock_app.dto.stock;

import com.coconut.stock_app.entity.cloud.Stock;
import com.coconut.stock_app.entity.cloud.StockChart;
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
    private String stockCode;//0
    private BigDecimal currentPrice;//2
    private BigDecimal openPrice;//7
    private BigDecimal highPrice;//8
    private BigDecimal lowPrice;//8
    private String versusSign;//3
    private BigDecimal contingentVol;//12
    private BigDecimal accumulatedVol;//13
    private BigDecimal accumulatedAmount; //14

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
                .stock(stock)
                .build();
    }
}
