package com.coconut.stock_app.repository.cloud;

import com.coconut.stock_app.entity.cloud.StockChart;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockChartRepository extends JpaRepository<StockChart, String> {
    // 특정 종목의 전체 차트 데이터 조회
    List<StockChart> findByStockStockCodeOrderByTimeAsc(String stockCode);

    // 특정 종목의 특정 시간 이후 데이터 조회
    List<StockChart> findByStockStockCodeAndTimeGreaterThanOrderByTimeAsc(
            String stockCode, String time);
}