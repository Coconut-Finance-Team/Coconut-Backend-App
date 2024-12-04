package com.coconut.stock_app.service;

import com.coconut.stock_app.dto.trade.OrderDTO;
import com.coconut.stock_app.entity.on_premise.Account;
import com.coconut.stock_app.entity.on_premise.Order;

public interface TradeService {
    void processBuyOrder(OrderDTO buyOrder, Account account);
    void processSellOrder(OrderDTO sellOrder, Account account);

}
