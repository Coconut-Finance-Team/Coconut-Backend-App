package com.coconut.stock_app.repository.cloud;

import com.coconut.stock_app.entity.cloud.StockChart;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockChartRepository extends JpaRepository<StockChart, Long> {

    // 주식 코드와 시간 범위에 따른 데이터 조회
    @Query("SELECT s FROM StockChart s WHERE s.stock.stockCode = :stockCode AND s.time >= :startTime ORDER BY s.time ASC")
    List<StockChart> findByStockCodeAndTimeRange(@Param("stockCode") String stockCode,
                                                 @Param("startTime") LocalDateTime startTime);

}
