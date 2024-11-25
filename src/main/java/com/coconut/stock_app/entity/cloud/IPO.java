package com.coconut.stock_app.entity.cloud;

import com.coconut.stock_app.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ipo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IPO extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long IPOId;

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
