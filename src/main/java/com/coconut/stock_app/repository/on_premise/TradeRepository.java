package com.coconut.stock_app.repository.on_premise;

import com.coconut.stock_app.entity.on_premise.Trade;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    @Query("""
    SELECT 
        COALESCE(SUM(t.tradePrice * t.tradeQuantity) / SUM(t.tradeQuantity), 0)
    FROM Trade t
    JOIN t.buyOrder o
    JOIN o.account a
    WHERE a.accountUuid = :accountUuid
    AND o.stockCode = :stockCode
""")
    BigDecimal findAveragePurchasePrice(@Param("accountUuid") String accountUuid,
                                        @Param("stockCode") String stockCode);

    @Query("""
        SELECT t 
        FROM Trade t 
        LEFT JOIN FETCH t.buyOrder b 
        LEFT JOIN FETCH t.sellOrder s 
        WHERE b.account.accountUuid = :accountUuid 
           OR s.account.accountUuid = :accountUuid 
        ORDER BY t.createdAt DESC
    """)
    //@EntityGraph(attributePaths = {"account"})
    List<Trade> findAllTradesByAccountUuid(@Param("accountUuid") String accountUuid);
}
