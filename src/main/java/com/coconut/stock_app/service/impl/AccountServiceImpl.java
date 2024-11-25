package com.coconut.stock_app.service.impl;

import com.coconut.stock_app.dto.account.AccountCreationRequest;
import com.coconut.stock_app.dto.account.AccountCreationResponse;
import com.coconut.stock_app.entity.cloud.StockChart;
import com.coconut.stock_app.entity.on_premise.Account;
import com.coconut.stock_app.entity.on_premise.AccountStatus;
import com.coconut.stock_app.entity.on_premise.User;
import com.coconut.stock_app.repository.cloud.StockChartRepository;
import com.coconut.stock_app.repository.on_premise.AccountRepository;
import com.coconut.stock_app.service.AccountService;
import com.coconut.stock_app.service.UserService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.RandomStringUtils;
import com.coconut.stock_app.dto.account.*;
import com.coconut.stock_app.entity.cloud.Stock;
import com.coconut.stock_app.entity.on_premise.*;
import com.coconut.stock_app.exception.CustomException;
import com.coconut.stock_app.exception.ErrorCode;
import com.coconut.stock_app.repository.cloud.StockRepository;
import com.coconut.stock_app.repository.on_premise.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private static final int ACCOUNT_NUMBER_LENGTH = 12;

    private final UserService userService;
    private final AccountRepository accountRepository;
    private final TradeRepository tradeRepository;
    private final TransactionRepository transactionRepository;
    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;
    private final ProfitLossRepository profitLossRepository;
    private final UserRepository userRepository;
    private final OwnedStockRepository ownedStockRepository;
    private final StockChartRepository stockChartRepository;

    @Override
    public AccountCreationResponse createAccount(AccountCreationRequest request) {
        // 사용자 검증
        User user = userService.verifyUser(request.getUsername(), request.getPhone(),
                request.getSocialSecurityNumber());

        // 고유 계좌번호 생성
        String accountId = generateUniqueAccountId();

        // 계좌 생성
        Account account = Account.builder().accountId(accountId).accountUuid(UUID.randomUUID().toString())
                .accountStatus(AccountStatus.OPEN).accountPurpose(request.getAccountPurpose())
                .accountAlias(request.getAccountAlias()).accountPassword(request.getAccountPassword())
                .deposit(BigDecimal.ZERO) // 초기 잔액 설정
                .reservedDeposit(BigDecimal.ZERO).user(user).build();

        accountRepository.save(account);

        // 사용자의 primaryAccount가 비어 있으면 설정
        if (user.getPrimaryAccount() == null) {
            user.setPrimaryAccount(account);
            userRepository.save(user); // User 엔티티 업데이트
        }

        return AccountCreationResponse.builder().accountId(account.getAccountId()).message("계좌가 성공적으로 생성되었습니다.")
                .build();
    }

    private String generateUniqueAccountId() {
        String accountId;
        do {
            accountId = RandomStringUtils.randomNumeric(ACCOUNT_NUMBER_LENGTH);
        } while (accountRepository.existsByAccountId(accountId)); // 중복 검사
        return accountId;
    }

    public AssetDTO getAsset(String uuid) {
        Account account = accountRepository.findByAccountUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_ACCOUNT));

        List<OwnedStock> ownedStocks = account.getOwnedStocks();
        BigDecimal investedAmount = BigDecimal.ZERO;

        for (OwnedStock ownedStock : ownedStocks) {
            investedAmount = investedAmount.add(ownedStock.getTotalPurchasePrice());
        }

        BigDecimal totalAssets = account.getDeposit().add(investedAmount);

        return AssetDTO.builder().accountAlias(account.getAccountAlias()).accountId(account.getAccountId())
                .totalAssets(totalAssets).deposit(account.getDeposit()).investedAmount(investedAmount).build();
    }

    public List<TransactionHistoryDTO> getTransactionsAll(String uuid) {
        List<TransactionHistoryDTO> tradeHistory = getTransactionsTxn(uuid);

        List<TransactionHistoryDTO> transactionHistory = getTransactionsDepositAndWithdrawals(uuid);

        List<TransactionHistoryDTO> combinedHistory = Stream.concat(tradeHistory.stream(), transactionHistory.stream())
                .sorted(Comparator.comparing(TransactionHistoryDTO::getDate).reversed()).collect(Collectors.toList());

        return combinedHistory;
    }

    public List<TransactionHistoryDTO> getTransactionsTxn(String uuid) {
        List<Trade> trades = tradeRepository.findAllTradesByAccountUuid(uuid);

        List<TransactionHistoryDTO> tradeHistory = trades.stream()
                .map(trade -> mapTradeToTransactionHistoryDTO(trade, uuid)).collect(Collectors.toList());
        return tradeHistory;
    }

    public List<TransactionHistoryDTO> getTransactionsDepositAndWithdrawals(String uuid) {
        List<Transaction> transactions = transactionRepository.findAllTransactionsByAccountUuid(uuid);

        List<TransactionHistoryDTO> transactionHistory = transactions.stream()
                .map(this::mapTransactionToTransactionHistoryDTO).collect(Collectors.toList());

        return transactionHistory;
    }

    public TradeDetailDTO getTradeDetail(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_TRADE));

        TradeDetailDTO tradeDetailDTO = TradeDetailDTO.builder()
                .totalPrice(trade.getTradePrice().multiply(new BigDecimal(trade.getTradeQuantity())))
                .quantity(trade.getTradeQuantity()).orderTime(trade.getCreatedAt()).build();
        return tradeDetailDTO;
    }

    public TransactionAmountDTO getTransactionsDepositsAndWithdrawalsDetail(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_TRANSACTION));

        TransactionAmountDTO transactionAmountDTO = TransactionAmountDTO.builder()
                .time(transaction.getTransactionDate()).amount(transaction.getAmount())
                .type(transaction.getTransactionType()).name(transaction.getName()).build();

        return transactionAmountDTO;
    }

    public List<OrderHistoryDTO> getAccountOrder(String uuid) {
        List<Order> orders = orderRepository.findAllOrdersByAccountId(uuid);

        List<OrderHistoryDTO> orderHistoryDTOS = orders.stream().map(this::mapOrderToOrderHistoryDTO)
                .collect(Collectors.toList());

        return orderHistoryDTOS;
    }

    public OrderHistoryDTO getOrderDetail(Long orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_ORDER));

        return mapOrderToOrderHistoryDTO(order);
    }

    public List<ProfitLossDTO> getAccountSalesProfit(String uuid) {
        List<ProfitLoss> profitLosses = profitLossRepository.findByAllProfitLossByAccountUuid(uuid);

        List<ProfitLossDTO> profitLossDTOS = profitLosses.stream().map(this::mapProfitLossToProfitLossDTO)
                .collect(Collectors.toList());

        return profitLossDTOS;
    }

    public ProfitLossDTO getAccountSalesProfitDetail(Long sales_id) {
        ProfitLoss profitLoss = profitLossRepository.findById(sales_id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_PROFIT_LOSS));

        ProfitLossDTO profitLossDTO = mapProfitLossToProfitLossDTO(profitLoss);

        return profitLossDTO;
    }

    public AccountDTO getAccount(String uuid) {
        Account account = accountRepository.findByAccountUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_ACCOUNT));

        AccountDTO accountDTO = new AccountDTO(account.getAccountId(), account.getCreatedAt());
        return accountDTO;
    }

    public List<InvestmentDTO> getInvestment(String uuid) {
        List<OwnedStock> ownedStocks = ownedStockRepository.findAllByAccountUuid(uuid);
        List<InvestmentDTO> investmentDTOS = ownedStocks.stream().map(this::mapOwnedStockToInvestmentDTO).
                collect(Collectors.toList());

        return investmentDTOS;
    }

    private InvestmentDTO mapOwnedStockToInvestmentDTO(OwnedStock ownedStock) {
        Stock stock = stockRepository.findByStockCode(ownedStock.getStockCode())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_STOCK));

        StockChart stockChart = stockChartRepository.findStockChartsByStockCode(stock.getStockCode(), PageRequest.of(0, 1))
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("No StockChart found for stockCode: " + stock.getStockCode()));

        BigDecimal pricePerShare = ownedStock.getTotalPurchasePrice().divide(new BigDecimal(ownedStock.getQuantity()));
        BigDecimal profit = stockChart.getCurrentPrice().subtract(pricePerShare);
        BigDecimal profitRate = profit.divide(pricePerShare, 4, RoundingMode.HALF_UP) // 소수점 4자리까지 계산
                .multiply(BigDecimal.valueOf(100));

        return InvestmentDTO.builder()
                .stockName(stock.getStockName())
                .stockCode(ownedStock.getStockCode())
                .price(stockChart.getCurrentPrice())
                .quantity(ownedStock.getQuantity())
                .profit(profit)
                .profitPercent(profitRate)
                .build();
    }
    private TransactionHistoryDTO mapTradeToTransactionHistoryDTO(Trade trade, String accountUuid) {
        String status;

        if (trade.getBuyOrder() != null && trade.getBuyOrder().getAccount().getAccountUuid().equals(accountUuid)) {
            status = "매수";
        } else if (trade.getSellOrder() != null && trade.getSellOrder().getAccount().getAccountUuid()
                .equals(accountUuid)) {
            status = "매도";
        } else {
            status = "알 수 없음";
        }
        Stock stock = stockRepository.findByStockCode(trade.getStockCode())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_STOCK));

        return TransactionHistoryDTO.builder().type("거래").date(trade.getCreatedAt().toLocalDate().toString())
                .name(stock.getStockName()).status(status)
                .amount(trade.getTradePrice().multiply(BigDecimal.valueOf(trade.getTradeQuantity())).longValue())
                .quantity(trade.getTradeQuantity()).id(trade.getTradeId()).build();
    }

    private TransactionHistoryDTO mapTransactionToTransactionHistoryDTO(Transaction transaction) {
        return TransactionHistoryDTO.builder().type("입출금").date(transaction.getTransactionDate().toString())
                .name(transaction.getName()).status(transaction.getTransactionType().name())
                .amount(transaction.getAmount().longValue()).quantity(null).id(transaction.getTransactionId()).build();
    }

    private OrderHistoryDTO mapOrderToOrderHistoryDTO(Order order) {

        Stock stock = stockRepository.findByStockCode(order.getStockCode())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_STOCK));

        String status;
        if (order.getOrderQuantity() == 0L && order.getOrderType() == OrderType.BUY) {
            status = "구매완료";
        } else if (order.getOrderQuantity() == 0L && order.getOrderType() == OrderType.SELL) {
            status = "판매완료";
        } else if (order.getOrderType() == OrderType.BUY) {
            status = "구매주문";
        } else if (order.getOrderType() == OrderType.SELL) {
            status = "판매주문";
        } else {
            status = "알 수 없음";
        }
        return OrderHistoryDTO.builder().stockName(stock.getStockName()).orderTime(order.getCreatedAt()).status(status)
                .type("지정가").id(order.getOrderId()).quantity(order.getInitQuantity()).price(order.getOrderPrice())
                .totalPrice(order.getOrderPrice().multiply(new BigDecimal(order.getInitQuantity()))).build();

    }

    private ProfitLossDTO mapProfitLossToProfitLossDTO(ProfitLoss profitLoss) {
        return ProfitLossDTO.builder().id(profitLoss.getProfitLossId()).stockCode(profitLoss.getStockCode())
                .stockName(profitLoss.getStockName()).purchasePricePerShare(profitLoss.getPurchasePricePerShare())
                .salePricePerShare(profitLoss.getSalePricePerShare()).profitRate(profitLoss.getProfitRate())
                .fee(profitLoss.getFee()).saleQuantity(profitLoss.getSaleQuantity()).build();
    }

}
