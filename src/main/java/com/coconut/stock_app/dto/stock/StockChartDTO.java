package com.coconut.stock_app.dto.stock;

import com.coconut.stock_app.entity.cloud.Stock;
import com.coconut.stock_app.entity.cloud.StockChart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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


    /**
     * StockChartDTO를 StockChart 엔티티로 변환하는 메서드
     *
     * @param stock Stock 엔티티 (ManyToOne 관계)
     * @return StockChart 엔티티
     */
    public StockChart toEntity(Stock stock) {
//        LocalDateTime tradeDateTime;
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//        try {
//            tradeDateTime = LocalDateTime.parse(this.time, formatter);
//        } catch (DateTimeParseException e) {
//            System.err.println("Date parsing error: " + e.getMessage());
//            tradeDateTime = LocalDateTime.now(); // 파싱 실패 시 현재 시간으로 대체 (필요 시 수정)
//        }

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
