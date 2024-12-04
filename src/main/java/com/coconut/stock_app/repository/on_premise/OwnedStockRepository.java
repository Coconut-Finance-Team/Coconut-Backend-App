package com.coconut.stock_app.repository.on_premise;

import com.coconut.stock_app.entity.cloud.StockChart;
import com.coconut.stock_app.entity.on_premise.OwnedStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OwnedStockRepository extends JpaRepository<OwnedStock, Long> {
    @Query("""
        SELECT os FROM OwnedStock os 
        WHERE os.stockCode = :stockCode 
        AND os.account.accountId = :accountId
    """)
    Optional<OwnedStock> findByStockCodeAndAccountId(@Param("stockCode") String stockCode,
                                                     @Param("accountId") String accountId);

    @Query("""
        SELECT o
        FROM OwnedStock o
        JOIN FETCH o.account a
        WHERE a.accountUuid = :accountUuid
        ORDER BY o.createdAt DESC
    """)
    List<OwnedStock> findAllByAccountUuid(@Param("accountUuid") String accountUuid);
}
