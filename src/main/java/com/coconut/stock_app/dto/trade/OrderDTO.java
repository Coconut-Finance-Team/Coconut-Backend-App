package com.coconut.stock_app.dto.trade;

import com.coconut.stock_app.entity.on_premise.OrderType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderDTO {
    private String stockName;
    private String stockCode;
    private Long orderQuantity;
    private BigDecimal orderPrice;
    private String accountUuId;
}
