package com.coconut.stock_app.service;

import com.coconut.stock_app.dto.trade.OrderDTO;
import com.coconut.stock_app.entity.on_premise.Order;

public interface TradeService {
    void processBuyOrder(OrderDTO buyOrder);
    void processSellOrder(OrderDTO sellOrder);

}
