package com.coconut.stock_app.service.impl;

import com.coconut.stock_app.dto.account.AccountTransactionResponseDTO;
import com.coconut.stock_app.dto.account.AssetDTO;
import com.coconut.stock_app.dto.account.TransactionDetailDTO;
import com.coconut.stock_app.dto.account.TransactionHistoryDTO;
import com.coconut.stock_app.entity.on_premise.Account;
import com.coconut.stock_app.entity.on_premise.OwnedStock;
import com.coconut.stock_app.entity.on_premise.Trade;
import com.coconut.stock_app.entity.on_premise.Transaction;
import com.coconut.stock_app.exception.CustomException;
import com.coconut.stock_app.exception.ErrorCode;
import com.coconut.stock_app.repository.on_premise.AccountRepository;
import com.coconut.stock_app.repository.on_premise.OwnedStockRepository;
import com.coconut.stock_app.repository.on_premise.TradeRepository;
import com.coconut.stock_app.repository.on_premise.TransactionRepository;
import com.coconut.stock_app.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TradeRepository tradeRepository;
    private final TransactionRepository transactionRepository;

    public AssetDTO getAsset(String uuid){
        Account account = accountRepository.findByAccountUuid(uuid)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_EXIST_ACCOUNT));

        List<OwnedStock> ownedStocks = account.getOwnedStocks();
        BigDecimal investedAmount = BigDecimal.ZERO;

        for(OwnedStock ownedStock : ownedStocks){
            investedAmount = investedAmount.add(ownedStock.getTotalPurchasePrice());
        }

        BigDecimal totalAssets = account.getDeposit().add(investedAmount);

        return AssetDTO.builder()
                .accountAlias(account.getAccountAlias())
                .accountId(account.getAccountId())
                .totalAssets(totalAssets)
                .deposit(account.getDeposit())
                .investedAmount(investedAmount)
                .build();
    }

    public List<TransactionHistoryDTO> getTransactionsAll(String uuid){


        List<TransactionHistoryDTO> tradeHistory = getTransactionsTxn(uuid);

        List<TransactionHistoryDTO> transactionHistory = getTransactionsDepositAndWithdrawals(uuid);

        List<TransactionHistoryDTO> combinedHistory = Stream.concat(tradeHistory.stream(), transactionHistory.stream())
                .sorted(Comparator.comparing(TransactionHistoryDTO::getDate).reversed())
                .collect(Collectors.toList());


        return combinedHistory;
    }

    public List<TransactionHistoryDTO> getTransactionsTxn(String uuid){
        List<Trade> trades = tradeRepository.findAllTradesByAccountUuid(uuid);

        List<TransactionHistoryDTO> tradeHistory = trades.stream()
                .map(trade -> mapTradeToTransactionHistoryDTO(trade, uuid))
                .collect(Collectors.toList());
        return tradeHistory;
    }

    public List<TransactionHistoryDTO> getTransactionsDepositAndWithdrawals(String uuid){
        List<Transaction> transactions = transactionRepository.findAllTransactionsByAccountUuid(uuid);

        List<TransactionHistoryDTO> transactionHistory = transactions.stream()
                .map(this::mapTransactionToTransactionHistoryDTO)
                .collect(Collectors.toList());

        return transactionHistory;
    }

    public TransactionDetailDTO getTransactionDetail(String uuid, Long tradeId){
        return null;
    }

    private TransactionHistoryDTO mapTradeToTransactionHistoryDTO(Trade trade, String accountUuid) {
        String status;
        if (trade.getBuyOrder() != null && trade.getBuyOrder().getAccount().getAccountUuid().equals(accountUuid)) {
            status = "매수";
        } else if (trade.getSellOrder() != null && trade.getSellOrder().getAccount().getAccountUuid().equals(accountUuid)) {
            status = "매도";
        } else {
            status = "알 수 없음";
        }

        return TransactionHistoryDTO.builder()
                .type("거래")
                .date(trade.getCreatedAt().toLocalDate().toString())
                .name(trade.getStockCode())
                .status(status)
                .amount(trade.getTradePrice().multiply(BigDecimal.valueOf(trade.getTradeQuantity())).longValue())
                .quantity(trade.getTradeQuantity())
                .build();
    }

    private TransactionHistoryDTO mapTransactionToTransactionHistoryDTO(Transaction transaction) {
        return TransactionHistoryDTO.builder()
                .type("입출금")
                .date(transaction.getTransactionDate().toString())
                .name(transaction.getDescription())
                .status(transaction.getTransactionType().name())
                .amount(transaction.getAmount().longValue())
                .quantity(null)
                .build();
    }
}
