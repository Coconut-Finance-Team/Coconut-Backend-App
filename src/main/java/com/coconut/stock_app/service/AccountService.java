package com.coconut.stock_app.service;

import com.coconut.stock_app.dto.account.*;
import java.util.List;

public interface AccountService {
    AssetDTO getAsset(String uuid);
    List<TransactionHistoryDTO> getTransactionsAll(String uuid);
    List<TransactionHistoryDTO> getTransactionsTxn(String uuid);
    List<TransactionHistoryDTO> getTransactionsDepositAndWithdrawals(String uuid);
    TradeDetailDTO getTradeDetail(Long tradeId);
    TransactionAmountDTO getTransactionsDepositsAndWithdrawalsDetail(Long transactionId);
    List<OrderHistoryDTO> getAccountOrder(String uuid);
    OrderHistoryDTO getOrderDetail(Long orderId);
    List<ProfitLossDTO> getAccountSalesProfit(String uuid);
    ProfitLossDTO getAccountSalesProfitDetail(Long sales_id);
    AccountDTO getAccount(String uuid);
    AccountCreationResponse createAccount(AccountCreationRequest request);
    List<InvestmentDTO> getInvestment(String uuid);
}
