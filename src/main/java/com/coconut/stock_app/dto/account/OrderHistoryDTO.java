package com.coconut.stock_app.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderHistoryDTO {
    private String stockName;
    private LocalDateTime orderTime;
    private String status;
    private String type;
    private Long quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
}
