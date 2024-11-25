package com.coconut.stock_app.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnedIpoDTO {
    private Long id;
    private String name;
    private Long quantity;
    private BigDecimal totalPrice;
    private LocalDate refundDate;
    private LocalDate listingDate;
    private LocalDate subscriptionDate;
}
