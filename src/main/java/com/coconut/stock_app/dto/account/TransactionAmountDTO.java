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
public class TransactionAmountDTO {
    private String type;            // 거래 유형 (예: 입금, 출금)
    private BigDecimal amount;      // 금액
    private LocalDateTime time;     // 거래 시간 (ISO 8601 형식)
}
