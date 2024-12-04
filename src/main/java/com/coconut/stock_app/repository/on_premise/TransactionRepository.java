package com.coconut.stock_app.repository.on_premise;

import com.coconut.stock_app.entity.on_premise.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("""
        SELECT tr 
        FROM Transaction tr 
        WHERE tr.account.accountUuid = :accountUuid 
        ORDER BY tr.transactionDate DESC
    """)
    List<Transaction> findAllTransactionsByAccountUuid(@Param("accountUuid") String accountUuid);
}
