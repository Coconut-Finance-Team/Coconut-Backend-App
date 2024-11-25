package com.coconut.stock_app.dto.ipo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class IPODTO {
    private Long id;

    private String category;

    private String companyName;

    private String leadUnderwriter; // 대표 주관회사

    private LocalDate subscriptionStartDate;

    private LocalDate subscriptionEndDate;

    private LocalDate refundDate;

    private LocalDate listingDate;

    private Long maxSubscriptionLimit; //최고 청약 한도

    private BigDecimal finalOfferPrice; // 확정 발행가
}
