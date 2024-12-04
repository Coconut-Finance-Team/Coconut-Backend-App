package com.coconut.stock_app.dto.account;

import com.coconut.stock_app.dto.account.TransactionHistoryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountTransactionResponseDTO {
    private Double returnRate;   // 수익률
    private List<TransactionHistoryDTO> transactionHistory; // 거래 내역 목록
}

