package com.coconut.stock_app.dto.stock;

import com.coconut.stock_app.entity.cloud.StockChart;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockChartResponse {
    private Long chartId;
    private String stockCode;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal currentPrice;
    private String versusSign;
    private BigDecimal contingentVol;
    private BigDecimal accumulatedVol;
    private BigDecimal accumulatedAmount;
    private String time;

    public static StockChartResponse fromEntity(StockChart entity) {
        return StockChartResponse.builder()
                .chartId(entity.getChartId())
                .stockCode(entity.getStock().getStockCode())
                .openPrice(entity.getOpenPrice())
                .highPrice(entity.getHighPrice())
                .lowPrice(entity.getLowPrice())
                .currentPrice(entity.getCurrentPrice())
                .versusSign(entity.getVersusSign())
                .contingentVol(entity.getContingentVol())
                .accumulatedVol(entity.getAccumulatedVol())
                .accumulatedAmount(entity.getAccumulatedAmount())
                .time(String.valueOf(entity.getTime()))
                .build();
    }
}
