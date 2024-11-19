package com.coconut.stock_app.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeDetailDTO {
    private BigDecimal totalPrice;        // 총 거래 금액
    private Long quantity;       // 거래 수량
    private LocalDateTime orderTime; // 주문 시간 (ISO 8601 형식)
}
