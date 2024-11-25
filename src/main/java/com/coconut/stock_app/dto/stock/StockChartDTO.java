package com.coconut.stock_app.dto.stock;

import com.coconut.stock_app.entity.cloud.Stock;
import com.coconut.stock_app.entity.cloud.StockChart;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        try {
            // HHmmss 형식의 문자열을 LocalTime으로 변환
            LocalTime localTime = LocalTime.parse(this.time, DateTimeFormatter.ofPattern("HHmmss"));

            // 현재 날짜와 결합하여 LocalDateTime 생성
            LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), localTime);

            return StockChart.builder()
                    .currentPrice(this.currentPrice)
                    .openPrice(this.openPrice)
                    .highPrice(this.highPrice)
                    .lowPrice(this.lowPrice)
                    .versusSign(this.versusSign)
                    .contingentVol(this.contingentVol)
                    .accumulatedVol(this.accumulatedVol)
                    .accumulatedAmount(this.accumulatedAmount)
                    .time(localDateTime)
                    .stock(stock)
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse time: " + this.time, e);
        }
    }
}
