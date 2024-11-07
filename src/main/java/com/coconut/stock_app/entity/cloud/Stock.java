package com.coconut.stock_app.entity.cloud;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {
    @Id
    @Column(length = 20)
    private String stockCode;

    @Column(nullable = false, length = 10)
    private String exchangeCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StocksStockStatusEnum stockStatus;

    @Column(nullable = false, length = 100)
    private String stockName;

    @Column(nullable = false)
    private LocalDate listedDate;

    private LocalDate delistedDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockChart> stockCharts;
}


enum StocksStockStatusEnum {
    ACTIVE,
    DELISTED
}
