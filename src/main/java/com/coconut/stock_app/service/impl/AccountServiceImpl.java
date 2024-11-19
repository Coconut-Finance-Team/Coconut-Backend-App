package com.coconut.stock_app.service.impl;

import com.coconut.stock_app.dto.account.*;
import com.coconut.stock_app.entity.cloud.Stock;
import com.coconut.stock_app.entity.on_premise.*;
import com.coconut.stock_app.exception.CustomException;
import com.coconut.stock_app.exception.ErrorCode;
import com.coconut.stock_app.repository.cloud.StockRepository;
import com.coconut.stock_app.repository.on_premise.AccountRepository;
import com.coconut.stock_app.repository.on_premise.OrderRepository;
import com.coconut.stock_app.repository.on_premise.TradeRepository;
import com.coconut.stock_app.repository.on_premise.TransactionRepository;
import com.coconut.stock_app.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;

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

    public TradeDetailDTO getTradeDetail(Long tradeId){
        Trade trade = tradeRepository.findById(tradeId).orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_TRADE));

        TradeDetailDTO tradeDetailDTO = TradeDetailDTO.builder()
                .totalPrice(trade.getTradePrice().multiply(new BigDecimal(trade.getTradeQuantity())))
                .quantity(trade.getTradeQuantity())
                .orderTime(trade.getCreatedAt())
                .build();
        return tradeDetailDTO;
    }

    public TransactionAmountDTO getTransactionsDepositsAndWithdrawalsDetail(Long transactionId){
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_TRANSACTION));

        TransactionAmountDTO transactionAmountDTO = TransactionAmountDTO.builder()
                .time(transaction.getTransactionDate())
                .amount(transaction.getAmount())
                .type(transaction.getTransactionType())
                .name(transaction.getName())
                .build();

        return transactionAmountDTO;
    }

    public List<OrderHistoryDTO> getAccountOrder(String uuid){
        List<Order> orders = orderRepository.findAllOrdersByAccountId(uuid);

        List<OrderHistoryDTO> orderHistoryDTOS = orders.stream()
                .map(this::mapOrderToOrderHistoryDTO)
                .collect(Collectors.toList());

        return orderHistoryDTOS;
    }

    public OrderHistoryDTO getOrderDetail(Long orderId){
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_ORDER));

        return mapOrderToOrderHistoryDTO(order);
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
        Stock stock = stockRepository.findByStockCode(trade.getStockCode())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_STOCK));

        return TransactionHistoryDTO.builder()
                .type("거래")
                .date(trade.getCreatedAt().toLocalDate().toString())
                .name(stock.getStockName())
                .status(status)
                .amount(trade.getTradePrice().multiply(BigDecimal.valueOf(trade.getTradeQuantity())).longValue())
                .quantity(trade.getTradeQuantity())
                .id(trade.getTradeId())
                .build();
    }

    private TransactionHistoryDTO mapTransactionToTransactionHistoryDTO(Transaction transaction) {
        return TransactionHistoryDTO.builder()
                .type("입출금")
                .date(transaction.getTransactionDate().toString())
                .name(transaction.getName())
                .status(transaction.getTransactionType().name())
                .amount(transaction.getAmount().longValue())
                .quantity(null)
                .id(transaction.getTransactionId())
                .build();
    }

    private OrderHistoryDTO mapOrderToOrderHistoryDTO(Order order) {

        Stock stock = stockRepository.findByStockCode(order.getStockCode())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_STOCK));

        String status;
        if(order.getOrderQuantity() == 0L && order.getOrderType() == OrderType.BUY) {
            status = "구매완료";
        }
        else if(order.getOrderQuantity() == 0L && order.getOrderType() == OrderType.SELL) {
            status = "판매완료";
        }
        else if(order.getOrderType() == OrderType.BUY) {
            status = "구매주문";
        }
        else if(order.getOrderType() == OrderType.SELL) {
            status = "판매주문";
        }
        else status = "알 수 없음";
        return OrderHistoryDTO.builder()
                .stockName(stock.getStockName())
                .orderTime(order.getCreatedAt())
                .status(status)
                .type("지정가")
                .id(order.getOrderId())
                .quantity(order.getInitQuantity())
                .price(order.getOrderPrice())
                .totalPrice(order.getOrderPrice().multiply(new BigDecimal(order.getInitQuantity())))
                .build();

    }
}
