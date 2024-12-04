package com.coconut.stock_app.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProfitLossDTO {
    private Long id;
    private String stockName;
    private String stockCode;
    private BigDecimal purchasePricePerShare;
    private BigDecimal salePricePerShare;
    private BigDecimal profitRate;
    private BigDecimal fee;
    private Long saleQuantity;
}
