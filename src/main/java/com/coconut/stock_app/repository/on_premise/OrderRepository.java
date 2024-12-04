package com.coconut.stock_app.repository.on_premise;

import com.coconut.stock_app.entity.on_premise.Order;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // 매수 주문 시, 체결 가능한 매도 주문을 비관적 잠금으로 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT o 
    FROM Order o 
    WHERE o.orderType = 'SELL' 
    AND o.orderPrice <= :buyPrice 
    AND o.orderQuantity > 0 
    AND o.account.accountId != :accountId
    ORDER BY o.createdAt ASC
""")
    Page<Order> findSellOrdersForBuy(@Param("buyPrice") BigDecimal buyPrice, @Param("accountId") String accountId, Pageable pageable);

    // 매도 주문 시, 체결 가능한 매수 주문을 비관적 잠금으로 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT o 
    FROM Order o 
    WHERE o.orderType = 'BUY' 
    AND o.orderPrice >= :sellPrice 
    AND o.orderQuantity > 0 
    AND o.account.accountId != :accountId
    ORDER BY o.createdAt ASC
""")
    Page<Order> findBuyOrdersForSell(@Param("sellPrice") BigDecimal sellPrice, @Param("accountId") String accountId, Pageable pageable);

    @Query("""
        SELECT o
        FROM Order o
        JOIN FETCH o.account a
        WHERE a.accountUuid = :accountUuid
        ORDER BY o.createdAt DESC
    """)
    List<Order> findAllOrdersByAccountId(@Param("accountUuid") String accountUuid);


    Optional<Order> findByOrderId(Long orderId);

}
