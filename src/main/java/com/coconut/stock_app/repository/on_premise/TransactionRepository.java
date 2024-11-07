package com.coconut.stock_app.repository.on_premise;

import com.coconut.stock_app.entity.on_premise.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
