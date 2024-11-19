package com.coconut.stock_app.repository.on_premise;

import com.coconut.stock_app.entity.on_premise.ProfitLoss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfitLossRepository extends JpaRepository<ProfitLoss, Long> {

    @Query("""
    SELECT p
    FROM ProfitLoss p
    JOIN FETCH p.order o 
    JOIN FETCH o.account a
    WHERE a.accountUuid = :accountUuid
    ORDER BY p.createdAt DESC
""")
    List<ProfitLoss> findByAllProfitLossByAccountUuid(String accountUuid);
}
