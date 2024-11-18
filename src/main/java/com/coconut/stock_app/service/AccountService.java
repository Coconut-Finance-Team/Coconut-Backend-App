package com.coconut.stock_app.service;

import com.coconut.stock_app.dto.account.AccountTransactionResponseDTO;
import com.coconut.stock_app.dto.account.AssetDTO;
import com.coconut.stock_app.dto.account.TransactionHistoryDTO;

import java.util.List;

public interface AccountService {
    AssetDTO getAsset(String uuid);
    List<TransactionHistoryDTO> getTransactionsAll(String uuid);
    List<TransactionHistoryDTO> getTransactionsTxn(String uuid);
    List<TransactionHistoryDTO> getTransactionsDepositAndWithdrawals(String uuid);
}
