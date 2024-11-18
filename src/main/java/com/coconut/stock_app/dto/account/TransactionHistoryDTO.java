package com.coconut.stock_app.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionHistoryDTO {
    private String type;       // 거래 or 환전
    private String date;       // 날짜 (형식: MM.dd)
    private String name;       // 종목 or 이름
    private String status;     // 판매, 구매, 이체입금 등
    private Long amount;       // 거래 금액
}
