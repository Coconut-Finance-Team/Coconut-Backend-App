package com.coconut.stock_app.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentDTO {
    private String stockName;
    private String stockCode;
    private Long quantity;
    private BigDecimal price;
    private BigDecimal profit;
    private BigDecimal profitPercent;
}
