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
public class TransactionDetailDTO {
    private String type;            // 거래 유형 (예: 구매, 판매)
    private String stockName;       // 주식 이름
    private BigDecimal totalPrice;        // 총 거래 금액
    private Long quantity;       // 거래 수량
    private LocalDateTime orderTime; // 주문 시간 (ISO 8601 형식)
}
