package com.coconut.stock_app.service.impl;

import com.coconut.stock_app.dto.trade.OrderDTO;
import com.coconut.stock_app.entity.on_premise.*;
import com.coconut.stock_app.exception.CustomException;
import com.coconut.stock_app.exception.ErrorCode;
import com.coconut.stock_app.repository.on_premise.*;
import com.coconut.stock_app.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {
    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;
    private final ProfitLossRepository profitLossRepository;
    private final AccountRepository accountRepository;
    private final OwnedStockRepository ownedStockRepository;

    public void processBuyOrder(OrderDTO buyOrderDTO){
        Account account = accountRepository.findByAccountUuid(buyOrderDTO.getAccountUuId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_ACCOUNT));

        BigDecimal totalCost = buyOrderDTO.getOrderPrice().multiply(new BigDecimal(buyOrderDTO.getOrderQuantity()));
        BigDecimal availableAmount = account.getDeposit().subtract(account.getReservedDeposit());

        if (availableAmount.compareTo(totalCost) < 0) {
            throw new CustomException(ErrorCode.INSUFFICIENT_FUNDS);
        }

        Order buyOrder = saveOrder(buyOrderDTO, OrderType.BUY, account);
        account.increaseReservedDeposit(totalCost);
        accountRepository.save(account);

        while(buyOrder.getOrderQuantity()>0){
            Page<Order> pageSellOrder = orderRepository.findSellOrdersForBuy(buyOrder.getOrderPrice(),buyOrder.getAccount().getAccountId(), PageRequest.of(0,1));

            if(!pageSellOrder.isEmpty()){
                Order sellOrder = pageSellOrder.getContent().get(0);
                Long quantity = processTrade(sellOrder,buyOrder);
                saveProfitLoss(sellOrder, buyOrderDTO.getStockName(), quantity);
            }
            else{
                break;
            }

        }
    }

    public void processSellOrder(OrderDTO sellOrderDTO){
        Account account = accountRepository.findByAccountUuid(sellOrderDTO.getAccountUuId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_ACCOUNT));


        Order sellOrder = saveOrder(sellOrderDTO, OrderType.SELL, account);
        boolean isTrade = true;
        Long quantity = 0L;

        while (sellOrder.getOrderQuantity() > 0) {
            Page<Order> pageBuyOrder = orderRepository.findBuyOrdersForSell(sellOrder.getOrderPrice(),sellOrder.getAccount().getAccountId(), PageRequest.of(0,1));
            if(!pageBuyOrder.isEmpty()){
                Order buyOrder = pageBuyOrder.getContent().get(0);
                quantity += processTrade(sellOrder,buyOrder);
            }
            else{
                isTrade = false;
                break;
            }
        }

        if(isTrade){
            saveProfitLoss(sellOrder, sellOrderDTO.getStockName(), quantity);
        }
    }

    private Long processTrade(Order sellOrder, Order buyOrder) {
        Long quantity = Math.min(sellOrder.getOrderQuantity(), buyOrder.getOrderQuantity());

        Trade trade = Trade.builder()
                .stockCode(sellOrder.getStockCode())
                .tradeQuantity(quantity)
                .tradePrice(sellOrder.getOrderPrice())
                .buyOrder(buyOrder)
                .sellOrder(sellOrder)
                .fee(BigDecimal.ZERO)
                .build();
        tradeRepository.save(trade);

        buyOrder.orderExecution(quantity);
        orderRepository.save(buyOrder);
        sellOrder.setOrderQuantity(quantity);
        orderRepository.save(sellOrder);

        updateAccountBalance(sellOrder, buyOrder, quantity);
        updateOwnedStock(trade);

        return quantity;
    }



    private Order saveOrder(OrderDTO order, OrderType orderType, Account account){
        Order newOrder = Order.builder()
                .stockCode(order.getStockCode())
                .orderQuantity(order.getOrderQuantity())
                .orderPrice(order.getOrderPrice())
                .orderType(orderType)
                .account(account)
                .build();
        return orderRepository.save(newOrder);
    }

    private void saveProfitLoss(Order sellOrder, String stockName, Long executedQuantity){
        Account account = sellOrder.getAccount();
        BigDecimal purchasePrice = tradeRepository.findAveragePurchasePrice(account.getAccountUuid(), sellOrder.getStockCode());
        ProfitLoss profitLoss = sellOrder.getProfitLoss();

        if(sellOrder.getProfitLoss()==null){
            profitLoss = ProfitLoss.builder()
                    .stockCode(sellOrder.getStockCode())
                    .stockName(stockName)
                    .purchasePricePerShare(purchasePrice)
                    .salePricePerShare(sellOrder.getOrderPrice())
                    .fee(new BigDecimal(0))
                    .saleQuantity(executedQuantity)
                    .account(account)
                    .order(sellOrder)
                    .build();
            profitLoss.calculateProfitRate();
            profitLossRepository.save(profitLoss);
            sellOrder.setProfitLoss(profitLoss);
            orderRepository.save(sellOrder);
        }
        else{
            profitLoss.increaseSaleQuantity(executedQuantity);
            profitLossRepository.save(profitLoss);
        }

    }

    private void updateAccountBalance(Order sellOrder, Order buyOrder, Long executedQuantity){
        Account sellerAccount = sellOrder.getAccount();
        Account buyerAccount = buyOrder.getAccount();

        BigDecimal amount = sellOrder.getOrderPrice().multiply(BigDecimal.valueOf(executedQuantity));

        sellerAccount.increaseBalance(amount);
        accountRepository.save(sellerAccount);

        buyerAccount.decreaseBalance(amount);
        accountRepository.save(buyerAccount);
    }

    private void updateOwnedStock(Trade trade) {
        Account buyerAccount = trade.getBuyOrder().getAccount();
        Account sellerAccount = trade.getSellOrder().getAccount();

        OwnedStock buyerOwnedStock = ownedStockRepository.findByStockCodeAndAccountId(trade.getStockCode(), buyerAccount.getAccountId())
                .orElse(null);
        OwnedStock sellerOwnedStock = ownedStockRepository.findByStockCodeAndAccountId(trade.getStockCode(), sellerAccount.getAccountId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_OWNED_STOCK));

        if (buyerOwnedStock == null) {
            buyerAccount.getOwnedStocks().add(
                    OwnedStock.builder()
                            .stockCode(trade.getStockCode())
                            .quantity(trade.getTradeQuantity())
                            .totalPurchasePrice(trade.getTradePrice().multiply(BigDecimal.valueOf(trade.getTradeQuantity())))
                            .account(buyerAccount)
                            .build()
            );
        } else {
            buyerOwnedStock.buyStock(trade.getTradeQuantity(), trade.getTradePrice());
            ownedStockRepository.save(buyerOwnedStock);
        }

        sellerOwnedStock.sellStock(trade.getTradeQuantity());

        if (sellerOwnedStock.getQuantity() == 0) ownedStockRepository.delete(sellerOwnedStock);
        else ownedStockRepository.save(sellerOwnedStock);
    }
}
