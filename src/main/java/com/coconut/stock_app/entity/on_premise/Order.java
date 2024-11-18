package com.coconut.stock_app.entity.on_premise;

import com.coconut.stock_app.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    private LocalDate orderDate;

    @Column(nullable = false, length = 20)
    private String stockCode;

    @Column(nullable = false)
    private int orderQuantity;

    @Column(nullable = false)
    private BigDecimal orderPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;


    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "accountId", nullable = false)
    private Account account;

    @OneToMany(mappedBy = "buyOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Trade> buyTrade;

    @OneToMany(mappedBy = "sellOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Trade> sellTrade;
}

