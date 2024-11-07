package com.coconut.stock_app.repository.cloud;

import com.coconut.stock_app.entity.cloud.StockChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockChartRepository extends JpaRepository<StockChart, Long> {
}
