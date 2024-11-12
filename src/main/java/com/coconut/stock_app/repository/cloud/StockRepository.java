package com.coconut.stock_app.repository.cloud;

import com.coconut.stock_app.entity.cloud.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Stock findByStockCode(String stockCode);
}
