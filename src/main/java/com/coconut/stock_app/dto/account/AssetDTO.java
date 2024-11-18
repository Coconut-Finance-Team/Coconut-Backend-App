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
public class AssetDTO {
    private String accountAlias;
    private String accountId;
    private BigDecimal totalAssets;
    private BigDecimal deposit;
    private BigDecimal investedAmount;
}
